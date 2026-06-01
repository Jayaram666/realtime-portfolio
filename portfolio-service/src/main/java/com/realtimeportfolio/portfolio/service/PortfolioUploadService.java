package com.realtimeportfolio.portfolio.service;

import com.realtimeportfolio.common.dto.PortfolioUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PortfolioUploadService {
    PortfolioUploadResponse uploadPortfolio(Long userId, MultipartFile file);
}