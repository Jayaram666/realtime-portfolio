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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

        List<PortfolioUploadRow> rows;
        String fileName = file.getOriginalFilename();

        // Dynamic router handles flat csv text strings safely bypassing Apache POI
        if (fileName != null && fileName.toLowerCase().endsWith(".csv")) {
            log.info("Processing flat file via native CSV text streaming routing engine");
            rows = parseCsvFile(file);
        } else {
            log.info("Processing binary workbook file via Apache POI extraction layout engines");
            rows = portfolioExcelParser.parse(file);
        }

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

    /**
     * Native lightweight stream reader extracting tabular content safely from flat texts
     */
    private List<PortfolioUploadRow> parseCsvFile(MultipartFile file) {
        List<PortfolioUploadRow> parsedRows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int currentRowIndex = 0;

            // Track dynamic header indices mapping arrays accurately
            int tickerIdx = -1, qtyIdx = -1, priceIdx = -1;

            while ((line = reader.readLine()) != null) {
                currentRowIndex++;

                // Skip fully blank line returns inside document streams
                if (line.trim().isBlank()) {
                    continue;
                }

                // Handle split array fragments isolating quote wrappers safely
                String[] tokens = line.split(",");
                for (int i = 0; i < tokens.length; i++) {
                    tokens[i] = tokens[i].trim().replace("^\"|\"$", "");
                }

                // Parse out column index placements using the header row
                if (currentRowIndex == 1) {
                    for (int i = 0; i < tokens.length; i++) {
                        String cleanHeader = tokens[i].trim();
                        if ("tickerSymbol".equalsIgnoreCase(cleanHeader)) tickerIdx = i;
                        else if ("quantity".equalsIgnoreCase(cleanHeader)) qtyIdx = i;
                        else if ("buyingPrice".equalsIgnoreCase(cleanHeader)) priceIdx = i;
                    }

                    // Fallback to strict layout ordering positions if headers aren't explicit
                    if (tickerIdx == -1 || qtyIdx == -1 || priceIdx == -1) {
                        tickerIdx = 1; // Column B
                        qtyIdx = 2;    // Column C
                        priceIdx = 3;  // Column D
                    }
                    continue;
                }

                // Process data records against parsed position references
                if (tokens.length > Math.max(tickerIdx, Math.max(qtyIdx, priceIdx))) {
                    try {
                        PortfolioUploadRow row = new PortfolioUploadRow();
                        row.setRowNumber(currentRowIndex);
                        row.setTickerSymbol(tokens[tickerIdx].toUpperCase());

                        // Handle numeric parsing logic
                        String rawQty = tokens[qtyIdx].replaceAll("[^0-9]", "");
                        row.setQuantity(rawQty.isEmpty() ? 0 : Integer.parseInt(rawQty));

                        String rawPrice = tokens[priceIdx].replaceAll("[^0-9.]", "");
                        row.setBuyingPrice(rawPrice.isEmpty() ? BigDecimal.ZERO : new BigDecimal(rawPrice));

                        parsedRows.add(row);
                    } catch (Exception parseException) {
                        log.warn("Skipping unparseable line record structural indices at line number: {}, error: {}",
                                currentRowIndex, parseException.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Severe system failure context tracking stream operations", ex);
            throw new RuntimeException("Failed parsing template layouts from file text packages: " + ex.getMessage());
        }

        return parsedRows;
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
        if (fileName == null) {
            throw new IllegalArgumentException("File reference missing system descriptor records");
        }

        String lowerName = fileName.toLowerCase();
        // Updated check rules accepting clean flat-text documents (.csv)
        if (!lowerName.endsWith(".xlsx") && !lowerName.endsWith(".csv")) {
            throw new IllegalArgumentException("Invalid file type. Only .xlsx and .csv files are accepted");
        }
    }
}
