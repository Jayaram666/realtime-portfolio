package com.realtimeportfolio.portfolio.repo;

import com.realtimeportfolio.portfolio.entity.MenuPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuPremissionRepository extends JpaRepository<MenuPermission, UUID> {

}
