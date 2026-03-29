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
import com.lowagie.text.pdf.draw.LineSeparator;
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
        // Génère le rapport de faisabilité pour ce Rapport
        return generateFeasibilityReport(id).readAllBytes();
    }

    @Override
    public void sendRapportByEmail(Long id, String email, String message)
            throws EntityNotFoundException, MessagingException {
        // Envoie le rapport de faisabilité par email
        sendFeasibilityReportByEmail(id, email, message);
    }

    @Override
    public ByteArrayInputStream generateProjectReport(Long projetId) throws EntityNotFoundException {
        ProjetDto projet = projetService.getProjectById(projetId);

        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Couleurs
            Color primaryBlue = Color.decode("#1A237E");
            Color primaryOrange = Color.decode("#FF8C00");
            Color lightGray = Color.decode("#F5F7FA");
            Color darkGray = Color.decode("#4B5563");

            // Polices
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, primaryBlue);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, primaryOrange);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, darkGray);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
            Font descriptionFont = FontFactory.getFont(FontFactory.HELVETICA, 10, darkGray);

            // === LOGOS (Eranove à gauche, GS2E à droite) ===
            try {
                PdfPTable logoTable = new PdfPTable(2);
                logoTable.setWidthPercentage(100);
                logoTable.setWidths(new float[] { 1, 1 });

                // Logo Eranove (gauche)
                PdfPCell leftCell = new PdfPCell();
                leftCell.setBorder(0);
                leftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                try {
                    com.lowagie.text.Image logoEranove = com.lowagie.text.Image
                            .getInstance(getClass().getClassLoader().getResource("images/logo-eranove.jpg"));
                    logoEranove.scaleToFit(120, 60);
                    logoEranove.setAlignment(Element.ALIGN_LEFT);
                    leftCell.addElement(logoEranove);
                } catch (Exception e) {
                    leftCell.addElement(new Phrase(""));
                }
                logoTable.addCell(leftCell);

                // Logo GS2E (droite)
                PdfPCell rightCell = new PdfPCell();
                rightCell.setBorder(0);
                rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                try {
                    com.lowagie.text.Image logoGs2e = com.lowagie.text.Image
                            .getInstance(getClass().getClassLoader().getResource("images/logo-gs2e.jpg"));
                    logoGs2e.scaleToFit(80, 80);
                    logoGs2e.setAlignment(Element.ALIGN_RIGHT);
                    rightCell.addElement(logoGs2e);
                } catch (Exception e) {
                    rightCell.addElement(new Phrase(""));
                }
                logoTable.addCell(rightCell);

                document.add(logoTable);
            } catch (Exception e) {
                log.warn("Logos non trouvés: {}", e.getMessage());
            }

            // === EN-TÊTE ===
            Paragraph header = new Paragraph("FICHE PROJET", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingBefore(10);
            header.setSpacingAfter(20);
            document.add(header);

            // Ligne de séparation
            LineSeparator separator = new LineSeparator();
            separator.setLineColor(primaryOrange);
            separator.setLineWidth(2f);
            document.add(new Chunk(separator));
            document.add(Chunk.NEWLINE);

            // === SECTION 1: INFORMATIONS GÉNÉRALES ===
            document.add(createSectionTitle("📋 INFORMATIONS GÉNÉRALES", sectionFont));

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[] { 1, 2 });
            infoTable.setSpacingBefore(10);
            infoTable.setSpacingAfter(15);

            addStyledRow(infoTable, "Nom du Projet", projet.getNom(), labelFont, valueFont, lightGray);
            addStyledRow(infoTable, "Type de Projet",
                    projet.getTypeProjetNom() != null ? projet.getTypeProjetNom() : "Non spécifié", labelFont,
                    valueFont, Color.WHITE);
            addStyledRow(infoTable, "Responsable", projet.getResponsable(), labelFont, valueFont, lightGray);
            addStyledRow(infoTable, "Site", projet.getSiteNom() != null ? projet.getSiteNom() : "Non spécifié",
                    labelFont, valueFont, Color.WHITE);
            addStyledRow(infoTable, "Priorité", projet.getPriorite() != null ? projet.getPriorite().name() : "N/A",
                    labelFont, valueFont, lightGray);
            addStyledRow(infoTable, "Statut", projet.getStatut() != null ? projet.getStatut().getLibelle() : "N/A",
                    labelFont, valueFont, Color.WHITE);

            document.add(infoTable);

            // === SECTION 2: DESCRIPTION ===
            document.add(createSectionTitle("📝 DESCRIPTION", sectionFont));

            Paragraph descParagraph = new Paragraph();
            descParagraph.setSpacingBefore(10);
            descParagraph.setSpacingAfter(15);
            String description = projet.getDescription() != null && !projet.getDescription().isEmpty()
                    ? projet.getDescription()
                    : "Aucune description fournie.";
            descParagraph.add(new Chunk(description, descriptionFont));
            document.add(descParagraph);

            // === SECTION 3: PLANIFICATION ===
            document.add(createSectionTitle("📅 PLANIFICATION", sectionFont));

            PdfPTable dateTable = new PdfPTable(2);
            dateTable.setWidthPercentage(100);
            dateTable.setWidths(new float[] { 1, 2 });
            dateTable.setSpacingBefore(10);
            dateTable.setSpacingAfter(15);

            addStyledRow(dateTable, "Date de Début", formatDate(projet.getDateDebut()), labelFont, valueFont,
                    lightGray);
            addStyledRow(dateTable, "Date de Fin Prévue", formatDate(projet.getDateFinPrevue()), labelFont, valueFont,
                    Color.WHITE);
            addStyledRow(dateTable, "Date de Fin Réelle", formatDate(projet.getDateFinReelle()), labelFont, valueFont,
                    lightGray);
            addStyledRow(dateTable, "Progression", projet.getProgression() + " %", labelFont, valueFont, Color.WHITE);

            document.add(dateTable);

            // === SECTION 4: BUDGET ===
            document.add(createSectionTitle("💰 BUDGET", sectionFont));

            PdfPTable budgetTable = new PdfPTable(2);
            budgetTable.setWidthPercentage(100);
            budgetTable.setWidths(new float[] { 1, 2 });
            budgetTable.setSpacingBefore(10);
            budgetTable.setSpacingAfter(15);

            String budgetStr = projet.getBudget() != null
                    ? String.format("%,.0f FCFA", projet.getBudget().doubleValue())
                    : "Non défini";
            addStyledRow(budgetTable, "Budget Alloué", budgetStr, labelFont, valueFont, lightGray);

            if (projet.getDureeJours() != null && projet.getDureeJours() > 0) {
                addStyledRow(budgetTable, "Durée Estimée", projet.getDureeJours() + " jours", labelFont, valueFont,
                        Color.WHITE);
            }
            if (projet.getCoutParJour() != null) {
                String coutJour = String.format("%,.0f FCFA/jour", projet.getCoutParJour().doubleValue());
                addStyledRow(budgetTable, "Coût par Jour", coutJour, labelFont, valueFont, lightGray);
            }

            document.add(budgetTable);

            // === SECTION 5: ÉQUIPE ===
            document.add(createSectionTitle("👥 ÉQUIPE DU PROJET", sectionFont));

            if (projet.getEquipe() != null && !projet.getEquipe().isEmpty()) {
                PdfPTable equipeTabe = new PdfPTable(1);
                equipeTabe.setWidthPercentage(100);
                equipeTabe.setSpacingBefore(10);
                equipeTabe.setSpacingAfter(15);

                int i = 0;
                for (String membre : projet.getEquipe()) {
                    PdfPCell cell = new PdfPCell(new Phrase("• " + membre, valueFont));
                    cell.setPadding(8);
                    cell.setBorderWidth(0);
                    cell.setBackgroundColor(i % 2 == 0 ? lightGray : Color.WHITE);
                    equipeTabe.addCell(cell);
                    i++;
                }
                document.add(equipeTabe);
            } else {
                document.add(new Paragraph("Aucun membre assigné.", descriptionFont));
            }

            // === SECTION 6: TAGS ===
            if (projet.getTags() != null && !projet.getTags().isEmpty()) {
                document.add(Chunk.NEWLINE);
                document.add(createSectionTitle("🏷️ MOTS-CLÉS", sectionFont));

                Paragraph tagsParagraph = new Paragraph();
                tagsParagraph.setSpacingBefore(10);
                tagsParagraph.add(new Chunk(projet.getTags(), valueFont));
                document.add(tagsParagraph);
            }

            // === PIED DE PAGE ===
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            LineSeparator footerSeparator = new LineSeparator();
            footerSeparator.setLineColor(lightGray);
            document.add(new Chunk(footerSeparator));

            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, darkGray);
            Paragraph footer = new Paragraph("Document généré le " + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(10);
            document.add(footer);

            document.close();
        } catch (DocumentException ex) {
            log.error("Error creating PDF: {}", ex.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private Paragraph createSectionTitle(String title, Font font) {
        Paragraph section = new Paragraph(title, font);
        section.setSpacingBefore(15);
        section.setSpacingAfter(5);
        return section;
    }

    private void addStyledRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont,
            Color bgColor) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, labelFont));
        cellLabel.setPadding(10);
        cellLabel.setBackgroundColor(bgColor);
        cellLabel.setBorderWidth(0.5f);
        cellLabel.setBorderColor(Color.decode("#E5E7EB"));
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(value != null ? value : "N/A", valueFont));
        cellValue.setPadding(10);
        cellValue.setBackgroundColor(bgColor);
        cellValue.setBorderWidth(0.5f);
        cellValue.setBorderColor(Color.decode("#E5E7EB"));
        table.addCell(cellValue);
    }

    private String formatDate(java.time.LocalDate date) {
        if (date == null)
            return "Non définie";
        return date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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

    @Override
    public ByteArrayInputStream generateFeasibilityReport(Long rapportId) throws EntityNotFoundException {
        Rapport rapport = rapportRepository.findById(rapportId)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé avec l'ID: " + rapportId));

        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Couleurs
            Color primaryBlue = Color.decode("#1A237E");
            Color primaryOrange = Color.decode("#FF8C00");
            Color lightGray = Color.decode("#F5F7FA");
            Color darkGray = Color.decode("#4B5563");
            Color successGreen = Color.decode("#10B981");
            Color warningYellow = Color.decode("#F59E0B");
            Color dangerRed = Color.decode("#EF4444");

            // Polices
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, primaryBlue);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, primaryOrange);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, darkGray);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
            Font descriptionFont = FontFactory.getFont(FontFactory.HELVETICA, 10, darkGray);

            // === LOGOS (Eranove à gauche, GS2E à droite) ===
            try {
                PdfPTable logoTable = new PdfPTable(2);
                logoTable.setWidthPercentage(100);
                logoTable.setWidths(new float[] { 1, 1 });

                // Logo Eranove (gauche)
                PdfPCell leftCell = new PdfPCell();
                leftCell.setBorder(0);
                leftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                try {
                    com.lowagie.text.Image logoEranove = com.lowagie.text.Image
                            .getInstance(getClass().getClassLoader().getResource("images/logo-eranove.jpg"));
                    logoEranove.scaleToFit(120, 60);
                    logoEranove.setAlignment(Element.ALIGN_LEFT);
                    leftCell.addElement(logoEranove);
                } catch (Exception e) {
                    leftCell.addElement(new Phrase(""));
                }
                logoTable.addCell(leftCell);

                // Logo GS2E (droite)
                PdfPCell rightCell = new PdfPCell();
                rightCell.setBorder(0);
                rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                try {
                    com.lowagie.text.Image logoGs2e = com.lowagie.text.Image
                            .getInstance(getClass().getClassLoader().getResource("images/logo-gs2e.jpg"));
                    logoGs2e.scaleToFit(80, 80);
                    logoGs2e.setAlignment(Element.ALIGN_RIGHT);
                    rightCell.addElement(logoGs2e);
                } catch (Exception e) {
                    rightCell.addElement(new Phrase(""));
                }
                logoTable.addCell(rightCell);

                document.add(logoTable);
            } catch (Exception e) {
                log.warn("Logos non trouvés: {}", e.getMessage());
            }

            // === EN-TÊTE ===
            Paragraph header = new Paragraph("RAPPORT DE FAISABILITÉ", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingBefore(10);
            header.setSpacingAfter(20);
            document.add(header);

            // Ligne de séparation
            LineSeparator separator = new LineSeparator();
            separator.setLineColor(primaryOrange);
            separator.setLineWidth(2f);
            document.add(new Chunk(separator));
            document.add(Chunk.NEWLINE);

            // === SECTION 1: INFORMATIONS DU RAPPORT ===
            document.add(createSectionTitle("📋 INFORMATIONS DU RAPPORT", sectionFont));

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[] { 1, 2 });
            infoTable.setSpacingBefore(10);
            infoTable.setSpacingAfter(15);

            addStyledRow(infoTable, "Nom du Rapport", rapport.getNom(), labelFont, valueFont, lightGray);
            addStyledRow(infoTable, "Projet Associé",
                    rapport.getProjet() != null ? rapport.getProjet().getNom() : "Non associé",
                    labelFont, valueFont, Color.WHITE);
            addStyledRow(infoTable, "Uploadé par", rapport.getUploadePar(), labelFont, valueFont, lightGray);
            addStyledRow(infoTable, "Type de fichier", rapport.getFichierType(), labelFont, valueFont, Color.WHITE);

            document.add(infoTable);

            // === SECTION 2: DESCRIPTION ===
            if (rapport.getDescription() != null && !rapport.getDescription().isEmpty()) {
                document.add(createSectionTitle("📝 DESCRIPTION", sectionFont));

                Paragraph descParagraph = new Paragraph();
                descParagraph.setSpacingBefore(10);
                descParagraph.setSpacingAfter(15);
                descParagraph.add(new Chunk(rapport.getDescription(), descriptionFont));
                document.add(descParagraph);
            }

            // === SECTION 3: ANALYSE DE FAISABILITÉ ===
            document.add(createSectionTitle("📊 ANALYSE DE FAISABILITÉ", sectionFont));

            PdfPTable faisabiliteTable = new PdfPTable(2);
            faisabiliteTable.setWidthPercentage(100);
            faisabiliteTable.setWidths(new float[] { 1, 2 });
            faisabiliteTable.setSpacingBefore(10);
            faisabiliteTable.setSpacingAfter(15);

            // Score de faisabilité avec code couleur
            String faisabiliteStr = rapport.getFaisabilite() + " %";
            Color faisabiliteColor = rapport.getFaisabilite() >= 70 ? successGreen
                    : rapport.getFaisabilite() >= 40 ? warningYellow : dangerRed;
            Font faisabiliteValueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, faisabiliteColor);

            PdfPCell labelCell = new PdfPCell(new Phrase("Taux de Faisabilité", labelFont));
            labelCell.setPadding(10);
            labelCell.setBackgroundColor(lightGray);
            labelCell.setBorderWidth(0.5f);
            labelCell.setBorderColor(Color.decode("#E5E7EB"));
            faisabiliteTable.addCell(labelCell);

            PdfPCell valueCell = new PdfPCell(new Phrase(faisabiliteStr, faisabiliteValueFont));
            valueCell.setPadding(10);
            valueCell.setBackgroundColor(lightGray);
            valueCell.setBorderWidth(0.5f);
            valueCell.setBorderColor(Color.decode("#E5E7EB"));
            faisabiliteTable.addCell(valueCell);

            // Niveau de risque avec code couleur
            String risqueStr = rapport.getRisque() != null ? rapport.getRisque().getLibelle() : "Non défini";
            Color risqueColor = Color.BLACK;
            if (rapport.getRisque() != null) {
                switch (rapport.getRisque()) {
                    case FAIBLE:
                        risqueColor = successGreen;
                        break;
                    case MOYEN:
                        risqueColor = warningYellow;
                        break;
                    case ELEVE:
                        risqueColor = Color.decode("#F97316");
                        break;
                    case CRITIQUE:
                        risqueColor = dangerRed;
                        break;
                }
            }
            Font risqueValueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, risqueColor);

            PdfPCell risqueLabelCell = new PdfPCell(new Phrase("Niveau de Risque", labelFont));
            risqueLabelCell.setPadding(10);
            risqueLabelCell.setBackgroundColor(Color.WHITE);
            risqueLabelCell.setBorderWidth(0.5f);
            risqueLabelCell.setBorderColor(Color.decode("#E5E7EB"));
            faisabiliteTable.addCell(risqueLabelCell);

            PdfPCell risqueValueCell = new PdfPCell(new Phrase(risqueStr, risqueValueFont));
            risqueValueCell.setPadding(10);
            risqueValueCell.setBackgroundColor(Color.WHITE);
            risqueValueCell.setBorderWidth(0.5f);
            risqueValueCell.setBorderColor(Color.decode("#E5E7EB"));
            faisabiliteTable.addCell(risqueValueCell);

            document.add(faisabiliteTable);

            // === SECTION 4: ESTIMATIONS ===
            document.add(createSectionTitle("💰 ESTIMATIONS", sectionFont));

            PdfPTable estimationTable = new PdfPTable(2);
            estimationTable.setWidthPercentage(100);
            estimationTable.setWidths(new float[] { 1, 2 });
            estimationTable.setSpacingBefore(10);
            estimationTable.setSpacingAfter(15);

            String budgetEstime = rapport.getBudgetEstime() != null
                    ? String.format("%,.0f FCFA", rapport.getBudgetEstime().doubleValue())
                    : "Non défini";
            addStyledRow(estimationTable, "Budget Estimé", budgetEstime, labelFont, valueFont, lightGray);

            String dureeEstimee = rapport.getDureeEstimeeMois() != null
                    ? rapport.getDureeEstimeeMois() + " mois"
                    : "Non définie";
            addStyledRow(estimationTable, "Durée Estimée", dureeEstimee, labelFont, valueFont, Color.WHITE);

            if (rapport.getAnalyseAutomatique() != null && rapport.getAnalyseAutomatique()) {
                addStyledRow(estimationTable, "Analyse", "Automatique ✓", labelFont, valueFont, lightGray);
            }

            document.add(estimationTable);

            // === SECTION 5: RECOMMANDATIONS ===
            if (rapport.getRecommandations() != null && !rapport.getRecommandations().isEmpty()) {
                document.add(createSectionTitle("💡 RECOMMANDATIONS", sectionFont));

                Paragraph recoParagraph = new Paragraph();
                recoParagraph.setSpacingBefore(10);
                recoParagraph.setSpacingAfter(15);
                recoParagraph.add(new Chunk(rapport.getRecommandations(), descriptionFont));
                document.add(recoParagraph);
            }

            // === PIED DE PAGE ===
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            LineSeparator footerSeparator = new LineSeparator();
            footerSeparator.setLineColor(lightGray);
            document.add(new Chunk(footerSeparator));

            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, darkGray);
            Paragraph footer = new Paragraph("Rapport de faisabilité généré le " + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(10);
            document.add(footer);

            document.close();
        } catch (DocumentException ex) {
            log.error("Error creating Feasibility Report PDF: {}", ex.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public void sendFeasibilityReportByEmail(Long rapportId, String toEmail, String message)
            throws EntityNotFoundException, MessagingException {
        Rapport rapport = rapportRepository.findById(rapportId)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé avec l'ID: " + rapportId));

        ByteArrayInputStream bis = generateFeasibilityReport(rapportId);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(toEmail);
        helper.setSubject("Rapport de Faisabilité : " + rapport.getNom());

        String emailBody = "Veuillez trouver ci-joint le rapport de faisabilité.";
        if (message != null && !message.isEmpty()) {
            emailBody = message + "\n\n" + emailBody;
        }
        helper.setText(emailBody);

        try {
            String filename = "Rapport_Faisabilite_" + rapport.getNom().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
            helper.addAttachment(filename, new ByteArrayResource(bis.readAllBytes()));
        } catch (Exception e) {
            throw new MessagingException("Erreur lors de l'attachement du PDF de faisabilité", e);
        }

        mailSender.send(mimeMessage);
        log.info("Rapport de faisabilité {} envoyé à {}", rapportId, toEmail);
    }
}