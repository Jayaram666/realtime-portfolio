package com.portfolio.web;

import com.portfolio.service.PortfolioUploadService;
import org.portfolio.dto.PortfolioUploadResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam("file") MultipartFile file
    ) {

        Long authenticatedUserId = 1L;

        return portfolioUploadService.uploadPortfolio(authenticatedUserId, file);
    }
}