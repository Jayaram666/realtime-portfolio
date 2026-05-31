package com.portfolio.service;

import org.portfolio.dto.PortfolioUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PortfolioUploadService {
    PortfolioUploadResponse uploadPortfolio(Long userId, MultipartFile file);
}