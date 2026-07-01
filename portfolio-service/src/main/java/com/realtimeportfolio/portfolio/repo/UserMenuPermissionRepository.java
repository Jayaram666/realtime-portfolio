package com.realtimeportfolio.portfolio.repo;

import com.realtimeportfolio.portfolio.entity.UserMenuPermission;
import com.realtimeportfolio.common.dto.MenuPermissionRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserMenuPermissionRepository extends JpaRepository<UserMenuPermission, UUID> {

    @Query("SELECT new com.realtimeportfolio.common.dto.MenuPermissionRow(" +
            "m.name, " +
            "m.label, " +
            "p.name) " +
            "FROM UserMenuPermission ump " +
            "JOIN ump.menuPermission mp " +
            "JOIN mp.menu m " +
            "JOIN mp.permission p " +
            "WHERE ump.user.id = :userId " +
            "AND m.active = true " +
            "ORDER BY m.displayOrder ASC")
    List<MenuPermissionRow> findMenuPermissionsByUserId(UUID userId);

}
