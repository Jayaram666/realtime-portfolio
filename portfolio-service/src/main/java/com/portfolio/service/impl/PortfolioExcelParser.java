package com.portfolio.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.portfolio.dto.PortfolioUploadRow;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class PortfolioExcelParser {

    public List<PortfolioUploadRow> parse(MultipartFile file) {
        List<PortfolioUploadRow> rows = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {

            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row excelRow = sheet.getRow(i);
                if (excelRow == null) continue; // skip empty rows

                String stockName = getStringValue(excelRow.getCell(0));
                String tickerSymbol = getStringValue(excelRow.getCell(1));
                Integer quantity = getIntegerValue(excelRow.getCell(2));
                BigDecimal buyingPrice = getBigDecimalValue(excelRow.getCell(3));
                rows.add(new PortfolioUploadRow(
                        i + 1,
                        stockName,
                        tickerSymbol,
                        quantity,
                        buyingPrice
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file", e);
        }
        return rows;
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return null;
        String value = cell.getStringCellValue().trim();
        return value.isEmpty() ? null : value;
    }

    private Integer getIntegerValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Expected numeric value but got: " + value);
            }
        } else {
            throw new RuntimeException("Unexpected cell type: " + cell.getCellType());
        }
    }

    private BigDecimal getBigDecimalValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }

        String value = getStringValue(cell);

        if (value == null || value.isBlank()) {
            return null;
        }

        return new BigDecimal(value);
    }
}
