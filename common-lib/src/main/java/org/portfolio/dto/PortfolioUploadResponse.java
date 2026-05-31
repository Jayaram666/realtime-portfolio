package org.portfolio.dto;


import java.util.ArrayList;
import java.util.List;

public class PortfolioUploadResponse {

    private int totalRows;
    private int insertedRows;
    private int updatedRows;
    private int failedRows;

    private List<String> errors = new ArrayList<>();

    public void incrementTotalRows() {
        this.totalRows++;
    }

    public void incrementInsertedRows() {
        this.insertedRows++;
    }

    public void incrementUpdatedRows() {
        this.updatedRows++;
    }

    public void addError(String error) {
        this.failedRows++;
        this.errors.add(error);
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getInsertedRows() {
        return insertedRows;
    }

    public int getUpdatedRows() {
        return updatedRows;
    }

    public int getFailedRows() {
        return failedRows;
    }

    public List<String> getErrors() {
        return errors;
    }
}