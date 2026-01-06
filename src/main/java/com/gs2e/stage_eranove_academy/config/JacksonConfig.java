package com.gs2e.stage_eranove_academy.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .build();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDate.class, new FlexibleLocalDateDeserializer());
        mapper.registerModule(module);

        return mapper;
    }

    /**
     * Désérialiseur personnalisé pour LocalDate qui accepte plusieurs formats
     */
    public static class FlexibleLocalDateDeserializer extends JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateString = p.getText();

            if (dateString == null || dateString.trim().isEmpty()) {
                return null;
            }

            // Essayer d'abord le format ISO_DATE_TIME (avec l'heure)
            try {
                // Extraire juste la partie date de "2025-06-15T00:00:00.000Z"
                if (dateString.contains("T")) {
                    dateString = dateString.substring(0, dateString.indexOf("T"));
                }
                return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException e) {
                // Si ça échoue, essayer le format simple
                try {
                    return LocalDate.parse(dateString);
                } catch (DateTimeParseException ex) {
                    throw new IOException("Impossible de parser la date: " + dateString, ex);
                }
            }
        }
    }
}
