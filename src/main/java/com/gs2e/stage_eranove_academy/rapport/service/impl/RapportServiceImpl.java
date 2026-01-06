package com.gs2e.stage_eranove_academy.rapport.service.impl;

import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.common.Exceptions.InvalidOperationException;
import com.gs2e.stage_eranove_academy.projet.dto.ProjetDto;
import com.gs2e.stage_eranove_academy.projet.service.ProjetService;
import com.gs2e.stage_eranove_academy.rapport.dto.RapportDto;
import com.gs2e.stage_eranove_academy.rapport.mapper.RapportMapper;
import com.gs2e.stage_eranove_academy.rapport.model.Rapport;
import com.gs2e.stage_eranove_academy.rapport.repository.RapportRepository;
import com.gs2e.stage_eranove_academy.rapport.service.RapportService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RapportServiceImpl implements RapportService {

    private final RapportRepository rapportRepository;
    private final RapportMapper rapportMapper;
    private final ProjetService projetService;
    private final JavaMailSender mailSender;

    @org.springframework.beans.factory.annotation.Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public Page<RapportDto> getAllRapports(Pageable pageable) {
        return rapportRepository.findAll(pageable).map(rapportMapper::toDto);
    }

    @Override
    public RapportDto getRapportById(Long id) throws EntityNotFoundException {
        return rapportRepository.findById(id)
                .map(rapportMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé avec l'ID: " + id));
    }

    @Override
    public RapportDto uploadRapport(MultipartFile file, RapportDto rapportDto, String uploadePar)
            throws IOException, InvalidOperationException {
        if (file.isEmpty()) {
            throw new InvalidOperationException("Le fichier est vide");
        }

        // Créer le répertoire s'il n'existe pas
        java.io.File directory = new java.io.File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Générer un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir, uniqueFilename);

        // Sauvegarder le fichier physiquement
        java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        Rapport rapport = rapportMapper.toEntity(rapportDto);
        rapport.setFichierNom(originalFilename);
        rapport.setFichierType(file.getContentType());
        rapport.setFichierTaille(file.getSize());
        rapport.setFichierChemin(filePath.toString());
        rapport.setUploadePar(uploadePar);

        Rapport saved = rapportRepository.save(rapport);
        return rapportMapper.toDto(saved);
    }

    @Override
    public RapportDto updateRapport(Long id, RapportDto rapportDto)
            throws EntityNotFoundException, InvalidOperationException {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé"));
        rapportMapper.updateEntityFromDto(rapportDto, rapport);
        return rapportMapper.toDto(rapportRepository.save(rapport));
    }

    @Override
    public void deleteRapport(Long id) throws EntityNotFoundException {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé"));

        // Supprimer le fichier physique
        try {
            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(rapport.getFichierChemin()));
        } catch (IOException e) {
            log.warn("Impossible de supprimer le fichier physique pour le rapport {}: {}", id, e.getMessage());
        }

        rapportRepository.deleteById(id);
    }

    @Override
    public Page<RapportDto> searchRapports(String nom, Long projetId, String fichierType, String uploadePar,
            Rapport.NiveauRisque risque, Integer minFaisabilite, Integer maxFaisabilite,
            LocalDateTime uploadDateFrom, LocalDateTime uploadDateTo, Pageable pageable) {
        return rapportRepository.searchRapports(nom, projetId, fichierType, uploadePar, risque,
                minFaisabilite, maxFaisabilite, uploadDateFrom, uploadDateTo, pageable)
                .map(rapportMapper::toDto);
    }

    @Override
    public byte[] downloadRapport(Long id) throws EntityNotFoundException {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé"));

        try {
            if (rapport.getFichierChemin() == null || rapport.getFichierChemin().isEmpty()) {
                log.error("Chemin du fichier est null ou vide pour le rapport {}", id);
                throw new EntityNotFoundException(
                        "Fichier non disponible pour ce rapport. Veuillez uploader à nouveau le document.");
            }

            java.nio.file.Path filePath = java.nio.file.Paths.get(rapport.getFichierChemin());

            if (!java.nio.file.Files.exists(filePath)) {
                log.error("Fichier non trouvé sur le disque: {}", rapport.getFichierChemin());
                throw new EntityNotFoundException(
                        "Fichier physique non trouvé. Ce rapport a peut-être été créé avant la mise en place du stockage. Veuillez uploader à nouveau le document.");
            }

            return java.nio.file.Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier pour le rapport {}: {}", id, e.getMessage());
            throw new EntityNotFoundException("Impossible de lire le fichier du rapport: " + e.getMessage());
        }
    }

    @Override
    public RapportDto analyzeRapport(Long id) throws EntityNotFoundException {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé"));
        rapport.setAnalyseAutomatique(true);
        // Simulation d'analyse
        rapport.setRisque(Rapport.NiveauRisque.MOYEN);
        rapport.setFaisabilite(75);
        return rapportMapper.toDto(rapportRepository.save(rapport));
    }

    @Override
    public Page<RapportDto> getRecentRapports(Pageable pageable) {
        return rapportRepository.findRecentRapports(pageable).map(rapportMapper::toDto);
    }

    @Override
    public List<RapportDto> getRapportsWithoutProject() {
        return rapportRepository.findRapportsWithoutProject(Pageable.unpaged())
                .getContent().stream()
                .map(rapportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, BigDecimal> getBudgetEstimatesByRiskLevel() {
        Map<String, BigDecimal> stats = new HashMap<>();
        for (Rapport.NiveauRisque risque : Rapport.NiveauRisque.values()) {
            BigDecimal sum = rapportRepository.sumBudgetByRiskLevel(risque);
            stats.put(risque.name(), sum != null ? sum : BigDecimal.ZERO);
        }
        return stats;
    }

    @Override
    public byte[] generatePDFReport(Long id) throws EntityNotFoundException {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé"));
        if (rapport.getProjet() == null) {
            throw new EntityNotFoundException("Ce rapport n'est associé à aucun projet");
        }
        return generateProjectReport(rapport.getProjet().getId()).readAllBytes();
    }

    @Override
    public void sendRapportByEmail(Long id, String email, String message)
            throws EntityNotFoundException, MessagingException {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé"));
        if (rapport.getProjet() == null) {
            throw new EntityNotFoundException("Ce rapport n'est associé à aucun projet");
        }
        sendProjectReportByEmail(rapport.getProjet().getId(), email);
    }

    @Override
    public ByteArrayInputStream generateProjectReport(Long projetId) throws EntityNotFoundException {
        ProjetDto projet = projetService.getProjectById(projetId);

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.decode("#1A237E"));
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.decode("#FF8C00"));
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);

            Paragraph header = new Paragraph("E-CARTO : FICHE PROJET", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(30);
            document.add(header);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            addTableRow(table, "Nom du Projet", projet.getNom(), titleFont, normalFont);
            addTableRow(table, "Responsable", projet.getResponsable(), titleFont, normalFont);
            addTableRow(table, "Statut", projet.getStatut().getLibelle(), titleFont, normalFont);
            addTableRow(table, "Budget", (projet.getBudget() != null ? projet.getBudget().toString() + " FCFA" : "N/A"),
                    titleFont, normalFont);
            addTableRow(table, "Site", projet.getSiteNom(), titleFont, normalFont);

            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            log.error("Error creating PDF: {}", ex.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, labelFont));
        cellLabel.setPadding(8);
        cellLabel.setBackgroundColor(Color.decode("#F5F7FA"));
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(value != null ? value : "N/A", valueFont));
        cellValue.setPadding(8);
        table.addCell(cellValue);
    }

    @Override
    public void sendProjectReportByEmail(Long projetId, String toEmail)
            throws EntityNotFoundException, MessagingException {
        ProjetDto projet = projetService.getProjectById(projetId);
        ByteArrayInputStream bis = generateProjectReport(projetId);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Rapport du Projet : " + projet.getNom());
        helper.setText("Veuillez trouver ci-joint le rapport détaillé.");

        try {
            helper.addAttachment("Rapport_Projet_" + projet.getNom() + ".pdf",
                    new ByteArrayResource(bis.readAllBytes()));
        } catch (Exception e) {
            throw new MessagingException("Erreur lors de l'attachement du PDF", e);
        }

        mailSender.send(message);
    }
}