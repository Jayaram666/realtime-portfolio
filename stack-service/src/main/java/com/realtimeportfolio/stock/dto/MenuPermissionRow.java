package com.realtimeportfolio.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuPermissionRow {

    private String menuName;
    private String menuLabel;
    private String permissionCode;
}
