package com.gs2e.stage_eranove_academy.export.service;

import com.gs2e.stage_eranove_academy.projet.dto.ProjetDto;
import com.gs2e.stage_eranove_academy.projet.service.ProjetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportExcelService {

    private final ProjetService projetService;

    public byte[] generateProjetsExcelReport() {
        log.info("Génération du rapport Excel des projets...");
        List<ProjetDto> projets = projetService.getAllProjects(Pageable.unpaged()).getContent();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Liste des Projets E-Carto");

            // Création des styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);

            // Création de l'en-tête
            String[] headers = {"ID", "Nom du Projet", "Statut", "Progression (%)", "Budget Global (FCFA)", "Date Début", "Date Fin Prévue", "Comité", "Entité", "Responsable"};
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Remplissage des données
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            int rowIndex = 1;
            for (ProjetDto projet : projets) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(projet.getId() != null ? projet.getId() : 0);
                row.createCell(1).setCellValue(projet.getNom() != null ? projet.getNom() : "");
                row.createCell(2).setCellValue(projet.getStatut() != null ? projet.getStatut().name() : "");
                
                Cell cellProgression = row.createCell(3);
                cellProgression.setCellValue(projet.getProgression() != null ? projet.getProgression() : 0);
                
                Cell cellBudget = row.createCell(4);
                if (projet.getBudget() != null) {
                    cellBudget.setCellValue(projet.getBudget().doubleValue());
                }

                row.createCell(5).setCellValue(projet.getDateDebut() != null ? projet.getDateDebut().format(formatter) : "");
                row.createCell(6).setCellValue(projet.getDateFinPrevue() != null ? projet.getDateFinPrevue().format(formatter) : "");
                row.createCell(7).setCellValue(projet.getComiteNom() != null ? projet.getComiteNom() : "");
                row.createCell(8).setCellValue(projet.getEntiteNom() != null ? projet.getEntiteNom() : "");
                row.createCell(9).setCellValue(projet.getResponsable() != null ? projet.getResponsable() : "");
            }

            // Auto-ajuster les colonnes
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Erreur lors de la génération Excel: ", e);
            throw new RuntimeException("Erreur lors de la génération du rapport Excel", e);
        }
    }
}
