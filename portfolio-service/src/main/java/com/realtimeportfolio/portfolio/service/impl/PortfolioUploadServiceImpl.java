package com.realtimeportfolio.portfolio.service.impl;


import com.realtimeportfolio.portfolio.entity.UserPortfolio;
import com.realtimeportfolio.portfolio.repo.UserPortfolioRepository;
import com.realtimeportfolio.portfolio.service.PortfolioUploadService;
import com.realtimeportfolio.portfolio.service.StackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.common.dto.PortfolioUploadResponse;
import com.realtimeportfolio.common.dto.PortfolioUploadRow;
import com.realtimeportfolio.common.dto.StockResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioUploadServiceImpl implements PortfolioUploadService {

    private final PortfolioExcelParser portfolioExcelParser;
    private final StackService stackService;
    private final UserPortfolioRepository userPortfolioRepository;

    @Override
    public PortfolioUploadResponse uploadPortfolio(UUID userId, MultipartFile file) {
        log.info("Received portfolio upload request for userId: {}, fileName: {}", userId, file.getOriginalFilename());
        validateFile(file);
        List<PortfolioUploadRow> rows = portfolioExcelParser.parse(file);
        PortfolioUploadResponse response = new PortfolioUploadResponse();
        for (PortfolioUploadRow row : rows) {
            response.incrementTotalRows();
            try {
                processRow(userId, row, response);
            } catch (Exception ex) {
                log.error("Error processing row: {}, error: {}", row, ex.getMessage());
                response.addError(
                        "Row " + row.getRowNumber() + ": " + ex.getMessage()
                );
            }

        }
        log.info("Portfolio upload completed for userId={}, totalRows={}, inserted={}, updated={}, failed={}",
                userId,
                response.getTotalRows(),
                response.getInsertedRows(),
                response.getUpdatedRows(),
                response.getFailedRows()
        );
        return response;
    }

    private void processRow(
            UUID userId,
            PortfolioUploadRow row,
            PortfolioUploadResponse response
    ) {
        validateRow(row);

        StockResponse stock = stackService
                .getStockByTickerSymbol(row.getTickerSymbol())
                .orElseThrow(() -> new RuntimeException(
                        "Invalid stock ticker symbol: " + row.getTickerSymbol()
                ));

        userPortfolioRepository
                .findByUserIdAndTickerSymbolIgnoreCase(userId, row.getTickerSymbol())
                .ifPresentOrElse(
                        existingPortfolio -> {
                            existingPortfolio.updatePortfolio(
                                    row.getQuantity(),
                                    row.getBuyingPrice()
                            );

                            userPortfolioRepository.save(existingPortfolio);
                            response.incrementUpdatedRows();

                            log.info("Portfolio updated. userId={}, tickerSymbol={}",
                                    userId,
                                    row.getTickerSymbol()
                            );
                        },
                        () -> {
                            UserPortfolio portfolio = new UserPortfolio();
                            portfolio.setUserId(userId);
                            portfolio.setCompanyName(stock.getCompanyName());
                            portfolio.setTickerSymbol(stock.getTickerSymbol());
                            portfolio.setQuantity(row.getQuantity());
                            portfolio.setBuyingPrice(row.getBuyingPrice());

                            userPortfolioRepository.save(portfolio);
                            response.incrementInsertedRows();

                            log.info("Portfolio inserted. userId={}, tickerSymbol={}",
                                    userId,
                                    row.getTickerSymbol()
                            );
                        }
                );
    }


    private void validateRow(PortfolioUploadRow row) {
        if (row.getTickerSymbol() == null || row.getTickerSymbol().isBlank()) {
            throw new RuntimeException("Ticker symbol is required");
        }

        if (row.getQuantity() == null || row.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        if (row.getBuyingPrice() == null || row.getBuyingPrice().signum() <= 0) {
            throw new RuntimeException("Buying price must be greater than zero");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.endsWith(".xlsx")) {
            throw new IllegalArgumentException("Invalid file type. Only .xlsx files are accepted");
        }
    }
}
