package com.gs2e.stage_eranove_academy.common.repository;

import com.gs2e.stage_eranove_academy.common.model.ParametreSysteme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour l'entité {@link ParametreSysteme}.
 * Permet d'accéder aux paramètres de configuration de l'application.
 */
@Repository
public interface ParametreSystemeRepository extends JpaRepository<ParametreSysteme, Long> {

    /**
     * Recherche un paramètre par sa clé unique.
     * 
     * @param cle La clé du paramètre (ex: "app.name", "jwt.secret")
     * @return L'entité si elle existe
     */
    Optional<ParametreSysteme> findByCle(String cle);

    /**
     * Vérifie si un paramètre existe par sa clé.
     * 
     * @param cle La clé du paramètre
     * @return true si le paramètre existe
     */
    boolean existsByCle(String cle);
}
