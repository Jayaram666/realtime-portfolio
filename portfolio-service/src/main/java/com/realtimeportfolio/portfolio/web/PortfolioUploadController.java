package com.realtimeportfolio.portfolio.web;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.service.PortfolioUploadService;
import com.realtimeportfolio.common.dto.PortfolioUploadResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioUploadController {

    private final PortfolioUploadService portfolioUploadService;

    public PortfolioUploadController(PortfolioUploadService portfolioUploadService) {
        this.portfolioUploadService = portfolioUploadService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public PortfolioUploadResponse uploadPortfolio(
            @RequestParam("file") MultipartFile file,@RequestHeader("X-User-Id") UUID authenticatedUserId
    ) {

        log.info("Portfolio upload API called. userId={}, fileName={}, size={}",
                authenticatedUserId,
                file != null ? file.getOriginalFilename() : null,
                file != null ? file.getSize() : 0);

        PortfolioUploadResponse response = portfolioUploadService.uploadPortfolio(authenticatedUserId, file);
        log.info("Portfolio upload API completed. userId={}, totalRows={}, failedRows={}",
                authenticatedUserId,
                response.getTotalRows(),
                response.getFailedRows());
        return response;
    }
}