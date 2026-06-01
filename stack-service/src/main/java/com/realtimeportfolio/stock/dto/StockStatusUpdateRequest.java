package com.realtimeportfolio.stock.dto;

import jakarta.validation.constraints.NotNull;

public class StockStatusUpdateRequest {

    @NotNull(message = "Active status is required")
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}