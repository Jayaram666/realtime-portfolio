package com.realtimeportfolio.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "user_menu_permission",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"menu_id", "user_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMenuPermission implements Persistable<UUID> {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "menu_permission_id", nullable = false)
    private MenuPermission menuPermission;

    public UserMenuPermission(UUID userId, UUID menuPermissionId) {
        // ⚡ Create a blank User shell object and assign its ID
        this.user = new User();
        this.user.setId(userId);

        // ⚡ Create a blank MenuPermission shell object and assign its ID
        this.menuPermission = new MenuPermission();
        this.menuPermission.setId(menuPermissionId);
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
