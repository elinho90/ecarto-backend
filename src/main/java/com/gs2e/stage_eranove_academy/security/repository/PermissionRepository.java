package com.gs2e.stage_eranove_academy.security.repository;

import com.gs2e.stage_eranove_academy.security.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query("SELECT p FROM Permission p WHERE p.role = :role")
    List<Permission> findByRole(@Param("role") String role);

    @Query("SELECT COUNT(p) > 0 FROM Permission p WHERE p.role = :role AND p.resource = :resource AND p.action = :action")
    boolean existsByRoleAndResourceAndAction(@Param("role") String role,
                                             @Param("resource") String resource,
                                             @Param("action") String action);

    @Query("SELECT p.action FROM Permission p WHERE p.role = :role AND p.resource = :resource")
    List<String> findActionsByRoleAndResource(@Param("role") String role,
                                              @Param("resource") String resource);
}