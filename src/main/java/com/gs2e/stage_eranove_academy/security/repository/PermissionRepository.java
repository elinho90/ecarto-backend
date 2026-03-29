package com.gs2e.stage_eranove_academy.security.repository;

import com.gs2e.stage_eranove_academy.security.model.Permission;

import java.util.List;

/**
 * DÉSACTIVÉ : Ce repository n'est plus un bean Spring JPA.
 * La table 'permissions' a été supprimée (V13) car elle n'était pas utilisée.
 * La gestion des droits se fait via l'enum Role dans Utilisateur.java.
 * Cette interface est conservée pour référence future.
 *
 * Pour réactiver : rajouter @Repository et extends JpaRepository<Permission,
 * Long>
 * et réactiver le @Entity dans Permission.java.
 */
public interface PermissionRepository {

    List<Permission> findByRole(String role);

    boolean existsByRoleAndResourceAndAction(String role, String resource, String action);

    List<String> findActionsByRoleAndResource(String role, String resource);
}