package com.gs2e.stage_eranove_academy.export.controller;

import com.gs2e.stage_eranove_academy.export.service.ExportExcelService;
import com.gs2e.stage_eranove_academy.export.service.ExportPdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/export")
@Tag(name = "Export / Reporting", description = "API de génération des exports Excel, PDF, CSV")
@Slf4j
@RequiredArgsConstructor
public class ExportController {

    private final ExportExcelService exportExcelService;
    private final ExportPdfService exportPdfService;

    @GetMapping("/projets/excel")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'DECIDEUR', 'CHEF_DE_PROJET', 'OBSERVATEUR')")
    @Operation(summary = "Exporter la liste de tous les projets au format Excel")
    public ResponseEntity<byte[]> exportProjetsExcel() {
        log.info("GET /api/export/projets/excel");
        byte[] excelContent = exportExcelService.generateProjetsExcelReport();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "e-carto_projets.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelContent);
    }

    @GetMapping("/projets/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR_SYSTEME', 'DECIDEUR', 'CHEF_DE_PROJET', 'OBSERVATEUR', 'ANALYSTE', 'DEVELOPPEUR')")
    @Operation(summary = "Générer une fiche projet détaillée au format PDF")
    public ResponseEntity<byte[]> exportProjetPdf(@PathVariable Long id) {
        log.info("GET /api/export/projets/{}/pdf", id);
        byte[] pdfContent = exportPdfService.generateProjetFichePdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "fiche_projet_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }
}
