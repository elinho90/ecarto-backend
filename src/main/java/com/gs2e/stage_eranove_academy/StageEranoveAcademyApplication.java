package com.gs2e.stage_eranove_academy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing // ✅ CRITIQUE pour vos AuditModel
@EnableTransactionManagement // ✅ Recommandé pour la cohérence
@EnableAsync // ✅ Pour les opérations asynchrones futures
@EnableScheduling // ✅ Pour les tâches planifiées futures
public class StageEranoveAcademyApplication {

    public static void main(String[] args) {
        // Configuration avancée du contexte Spring
        SpringApplication.run(StageEranoveAcademyApplication.class, args);
    }

}