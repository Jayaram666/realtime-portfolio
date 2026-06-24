package com.realtimeportfolio.portfolio.service;

import com.realtimeportfolio.common.dto.PortfolioUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface PortfolioUploadService {
    PortfolioUploadResponse uploadPortfolio(UUID userId, MultipartFile file);
}