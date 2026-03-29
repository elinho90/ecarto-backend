package com.gs2e.stage_eranove_academy.rapport.service;

import com.gs2e.stage_eranove_academy.common.Exceptions.EntityNotFoundException;
import com.gs2e.stage_eranove_academy.common.Exceptions.InvalidOperationException;
import com.gs2e.stage_eranove_academy.rapport.dto.RapportDto;
import com.gs2e.stage_eranove_academy.rapport.model.Rapport;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RapportService {
        Page<RapportDto> getAllRapports(Pageable pageable);

        RapportDto getRapportById(Long id) throws EntityNotFoundException;

        RapportDto uploadRapport(MultipartFile file, RapportDto rapportDto, String uploadePar)
                        throws IOException, InvalidOperationException;

        RapportDto updateRapport(Long id, RapportDto rapportDto)
                        throws EntityNotFoundException, InvalidOperationException;

        void deleteRapport(Long id) throws EntityNotFoundException;

        Page<RapportDto> searchRapports(String nom, Long projetId, String fichierType, String uploadePar,
                        Rapport.NiveauRisque risque, Integer minFaisabilite, Integer maxFaisabilite,
                        LocalDateTime uploadDateFrom, LocalDateTime uploadDateTo, Pageable pageable);

        byte[] downloadRapport(Long id) throws EntityNotFoundException, IOException;

        RapportDto analyzeRapport(Long id) throws EntityNotFoundException, InvalidOperationException;

        Page<RapportDto> getRecentRapports(Pageable pageable);

        List<RapportDto> getRapportsWithoutProject();

        Map<String, BigDecimal> getBudgetEstimatesByRiskLevel();

        byte[] generatePDFReport(Long id) throws EntityNotFoundException, IOException;

        void sendRapportByEmail(Long id, String email, String message)
                        throws EntityNotFoundException, MessagingException;

        // Métriques pour le projet (Nouveau flux)
        ByteArrayInputStream generateProjectReport(Long projetId) throws EntityNotFoundException;

        void sendProjectReportByEmail(Long projetId, String toEmail) throws EntityNotFoundException, MessagingException;

        // Génération du rapport de faisabilité (distinct de la fiche projet)
        ByteArrayInputStream generateFeasibilityReport(Long rapportId) throws EntityNotFoundException;

        // Envoi du rapport de faisabilité par email
        void sendFeasibilityReportByEmail(Long rapportId, String toEmail, String message)
                        throws EntityNotFoundException, MessagingException;
}