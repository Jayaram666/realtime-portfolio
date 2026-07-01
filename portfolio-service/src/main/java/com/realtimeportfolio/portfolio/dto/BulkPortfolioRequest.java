package com.realtimeportfolio.portfolio.dto;

import com.realtimeportfolio.common.dto.CreatePortfolioRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BulkPortfolioRequest {

    @NotEmpty(message = "The portfolio items list cannot be empty")
    @Valid
    private List<CreatePortfolioRequest> items;
}