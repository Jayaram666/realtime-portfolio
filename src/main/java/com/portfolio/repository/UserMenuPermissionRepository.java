package com.portfolio.repository;

import com.portfolio.dto.MenuPermissionRow;
import com.portfolio.entity.UserMenuPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserMenuPermissionRepository extends JpaRepository<UserMenuPermission, Long> {

    @Query("SELECT new com.portfolio.dto.MenuPermissionRow(" +
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
    List<MenuPermissionRow> findMenuPermissionsByUserId(Long userId);
}
