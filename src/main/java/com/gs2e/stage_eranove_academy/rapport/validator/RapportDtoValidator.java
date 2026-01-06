package com.gs2e.stage_eranove_academy.rapport.validator;

import com.gs2e.stage_eranove_academy.common.Exceptions.InvalidOperationException;
import com.gs2e.stage_eranove_academy.rapport.dto.RapportDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class RapportDtoValidator {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "doc", "docx");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final Tika tika = new Tika();

    public void validate(RapportDto rapportDto) {
        if (rapportDto == null) {
            throw new InvalidOperationException("Les données du rapport ne peuvent pas être nulles");
        }

        // Validation métier supplémentaire
        if (rapportDto.getFaisabilite() != null && rapportDto.getRisque() != null) {
            validateFaisabiliteRisqueCoherence(rapportDto);
        }
    }

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidOperationException("Le fichier ne peut pas être vide");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidOperationException("La taille du fichier ne peut pas dépasser 10MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new InvalidOperationException("Le nom du fichier est requis");
        }

        // Vérification de l'extension
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidOperationException("Type de fichier non supporté. Extensions autorisées : pdf, doc, docx");
        }

        // Vérification du type MIME réel
        try {
            String mimeType = tika.detect(file.getInputStream());
            if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
                throw new InvalidOperationException("Type de contenu invalide. Fichier potentiellement dangereux.");
            }
        } catch (IOException e) {
            log.error("Erreur lors de la détection du type MIME", e);
            throw new InvalidOperationException("Impossible de valider le type de fichier");
        }

        // Protection contre le path traversal
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new InvalidOperationException("Nom de fichier invalide");
        }
    }

    private void validateFaisabiliteRisqueCoherence(RapportDto dto) {
        // Exemple de validation métier
        if (dto.getFaisabilite() > 80 && dto.getRisque().name().equals("CRITIQUE")) {
            log.warn("Incohérence : faisabilité élevée avec risque critique");
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
}