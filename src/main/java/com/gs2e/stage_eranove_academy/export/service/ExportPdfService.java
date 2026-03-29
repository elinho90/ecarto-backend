package com.gs2e.stage_eranove_academy.export.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.gs2e.stage_eranove_academy.projet.dto.ProjetDto;
import com.gs2e.stage_eranove_academy.projet.service.ProjetService;
import com.gs2e.stage_eranove_academy.phase.dto.PhaseDto;
import com.gs2e.stage_eranove_academy.phase.service.PhaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportPdfService {

    private final ProjetService projetService;
    private final PhaseService phaseService;

    public byte[] generateProjetFichePdf(Long projetId) {
        log.info("Génération de la fiche projet PDF pour l'ID: {}", projetId);
        
        ProjetDto projet = projetService.getProjectById(projetId);
        List<PhaseDto> phases = phaseService.getPhasesByProjet(projetId);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);

            // Titre Principal
            Paragraph title = new Paragraph("Fiche Projet : " + projet.getNom(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // 1. Informations Générales
            document.add(new Paragraph("1. Informations Générales", headerFont));
            document.add(new Paragraph("Statut : " + (projet.getStatut() != null ? projet.getStatut().name() : "N/A"), boldFont));
            document.add(new Paragraph("Priorité : " + (projet.getPriorite() != null ? projet.getPriorite().name() : "N/A"), normalFont));
            document.add(new Paragraph("Progression : " + projet.getProgression() + "%", boldFont));
            document.add(new Paragraph("Responsable : " + (projet.getResponsable() != null ? projet.getResponsable() : "Non assigné"), normalFont));
            document.add(new Paragraph("Comité Rattaché : " + (projet.getComiteNom() != null ? projet.getComiteNom() : "N/A"), normalFont));
            document.add(new Paragraph("Entité : " + (projet.getEntiteNom() != null ? projet.getEntiteNom() : "N/A"), normalFont));
            if (projet.getDateDebut() != null) document.add(new Paragraph("Date de début : " + projet.getDateDebut().format(formatter), normalFont));
            if (projet.getDateFinPrevue() != null) document.add(new Paragraph("Date de fin prévue : " + projet.getDateFinPrevue().format(formatter), normalFont));
            document.add(new Paragraph(" ", normalFont)); // Spacing

            // 2. Budget
            document.add(new Paragraph("2. Budget & Ressources", headerFont));
            document.add(new Paragraph("Budget Alloué : " + (projet.getBudget() != null ? projet.getBudget() + " FCFA" : "Non défini"), normalFont));
            document.add(new Paragraph("Budget Consommé : " + (projet.getBudgetConsomme() != null ? projet.getBudgetConsomme() + " FCFA" : "0 FCFA"), normalFont));
            document.add(new Paragraph(" ", normalFont)); // Spacing

            // 3. Phasing Logique
            document.add(new Paragraph("3. État d'Avancement des Phases", headerFont));
            
            if (phases.isEmpty()) {
                document.add(new Paragraph("Aucune phase définie pour ce projet.", normalFont));
            } else {
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                
                // Table header
                PdfPCell cell = new PdfPCell(new Phrase("Phase", boldFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                
                cell = new PdfPCell(new Phrase("Statut", boldFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                
                cell = new PdfPCell(new Phrase("Dates", boldFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                
                cell = new PdfPCell(new Phrase("Progression", boldFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                
                // Table data
                for (PhaseDto p : phases) {
                    table.addCell(p.getNom());
                    table.addCell(p.getStatut() != null ? p.getStatut().name() : "");
                    
                    String dates = "";
                    if (p.getDateDebutPrevue() != null) dates += p.getDateDebutPrevue().format(formatter);
                    if (p.getDateFinPrevue() != null) dates += " => " + p.getDateFinPrevue().format(formatter);
                    table.addCell(dates);
                    
                    table.addCell(p.getProgression() + " %");
                }
                document.add(table);
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Erreur lors de la génération PDF: ", e);
            throw new RuntimeException("Erreur lors de la génération du rapport PDF", e);
        }
    }
}
