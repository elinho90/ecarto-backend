package com.gs2e.stage_eranove_academy.common.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.gs2e.stage_eranove_academy.common.Exceptions.ApiErrorCode;
import com.gs2e.stage_eranove_academy.common.Exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.FileWriter;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class Utilities {

    public static String username;
    @Value("${kafka.bootstrap-servers}")
    static String bootstrapServer;

    public static Map<String, Object> pagingResponse(Object listOfValue, Page<?> page, boolean status) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("data", listOfValue);
        response.put("currentPage", page.getNumber());
        response.put("recordsTotal", page.getTotalElements());
        return response;
    }

    public static Map<String, Object> singleResponse(Object object, boolean status) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("data", object);
        return response;
    }

    public static Map<String, Object> pagingResponseEmpty(boolean status, String errorCode, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("error_code", errorCode);
        response.put("error_message", message);
        return response;
    }

    public static Map<String, Object> fileWriter(String fileName, String data) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName + ".req");
        fileWriter.write(data);
        fileWriter.close();
        return null;
    }

    public static long calculSuperficie(long longueur, long hauteur) {
        return longueur * hauteur;
    }

    public static double convertirCentimetreToMetre(long longueur) {
        return longueur / 100;
    }

    public static <T> ResponseEntity<Map<String, Object>> getMapResponseEntity(Page<T> pages) {
        List<T> content = pages.getContent();

        if (content.isEmpty()) {
            return new ResponseEntity<>(Utilities.pagingResponseEmpty(false, "4004", "Aucune donnée retournée"),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(Utilities.pagingResponse(content, pages, true), HttpStatus.OK);
    }

    public static <T> T applyPatch(JsonPatch patch, T targetObject, Class<T> targetType)
            throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(targetObject, JsonNode.class));
        return objectMapper.treeToValue(patched, targetType);
    }

    public static String convertInstantToLocalDateFormattedd(Instant instant) {
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy
        // HH'h':mm'min':ss'sec'");
        return localDate.format(formatter);
    }

    public static String convertInstantToLocalDateTimeFormatted(Instant instant) {
        // LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime localDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy
        // HH'h':mm'min':ss'sec'");
        return localDate.format(formatter);
    }

    public static String convertDateToStringFormatted(Date date) {
        // Définir le format de date souhaité
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Formater la date
        return formatter.format(date);
    }

    public static String convertDateToString(Date date) {
        // Définir le format de date souhaité
        SimpleDateFormat formatter = new SimpleDateFormat("yyy-MM-dd");
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Formater la date
        return formatter.format(date);
    }

    public static Date convertStringToDate(String dateString) throws ParseException {
        // Définir le format de date souhaité
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Parser la chaîne de caractères en Date
        return formatter.parse(dateString);
    }

    public static Instant convertStringToInstant(String dateString) {
        // Utiliser le format ISO 8601 pour analyser la chaîne en Instant
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        return Instant.from(formatter.parse(dateString));
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (!(dateString.length() >= 11)) {
            return LocalDateTime.parse(dateString + " 00:00:00", formatter);
        }
        return LocalDateTime.parse(dateString, formatter);
    }

    // <T> T checkFound(T object, String msg)

    public static ResponseEntity<Map<String, Object>> createSuccessResponse(HttpStatus status, Page<?> page,
            String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("data", page.getContent());
        response.put("error_message", message);
        response.put("current_page", page.getNumber());
        response.put("total_items", page.getTotalElements());
        response.put("total_pages", page.getTotalPages());
        response.put("page_size", page.getSize());
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<Map<String, Object>> createSuccessResponse(HttpStatus status, List<T> list,
            String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("data", list);
        response.put("succes_message", message);
        response.put("total_items", list.size());
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<Map<String, Object>> createSuccessResponse(HttpStatus status, Object data,
            String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("data", data);
        response.put("succes_message", message);
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status,
            String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", false);
        response.put("data", errorMessage);
        response.put("error_message", message);
        return ResponseEntity.status(status).body(response);
    }

    public static <T, ID> T findEntityById(JpaRepository<T, ID> repository, ID id, String message) {
        Optional<T> optionalEntity = repository.findById(id);
        return optionalEntity.orElseThrow(() -> {
            log.error(message);
            return new ApiException(ApiErrorCode.ENTITY_NOT_FOUND.getValue(), ApiErrorCode.ENTITY_NOT_FOUND, message,
                    HttpStatus.OK);
        });
    }

    public static <T, ID> T findEntityById(JpaRepository<T, ID> repository, ID id, ApiException ex, String message) {
        Optional<T> optionalEntity = repository.findById(id);
        return optionalEntity.orElseThrow(() -> {
            log.error(message);
            return ex;
        });
    }

    /**
     * Java: Formatting byte size to human readable format
     * SI (1 k = 1,000)
     * Source:
     * https://programming.guide/java/formatting-byte-size-to-human-readable-format.html
     * 
     * @param bytes
     * @return
     */
    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    /**
     * Java: Formatting byte size to human readable format
     * Binary (1 K = 1,024)
     * Source:
     * https://programming.guide/java/formatting-byte-size-to-human-readable-format.html
     * 
     * @param bytes
     * @return
     */
    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

}
