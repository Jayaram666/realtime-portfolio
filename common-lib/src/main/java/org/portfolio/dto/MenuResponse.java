package org.portfolio.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class MenuResponse {

    private String label;
    private String menuOption;
    private List<String> permissions;

    public MenuResponse(String label, String menuOption, List<String> permissions) {
        this.label = label;
        this.menuOption = menuOption;
        this.permissions = permissions;
    }

   public static class MenuResponseBuilder {
        private String label;
        private String menuOption;
        private List<String> permissions = new ArrayList<>();

        public MenuResponseBuilder setLabel(String label) {
            this.label = label;
            return this;
        }

        public MenuResponseBuilder setMenuOption(String menuOption) {
            this.menuOption = menuOption;
            return this;
        }

        public void setPermissions(String permission) {
             this.permissions.add(permission) ;
        }

        public MenuResponse build() {
            return new MenuResponse(label, menuOption, permissions);
        }
        private MenuResponse buildMenuResponse() {
            return new MenuResponse(label, menuOption, permissions);
        }
    }

}
