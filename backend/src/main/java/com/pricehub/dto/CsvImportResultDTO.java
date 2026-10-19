package com.pricehub.dto;

import java.util.ArrayList;
import java.util.List;

public class CsvImportResultDTO {

    private int totalRowsRead;
    private int importedCount;
    private int updatedCount;
    private int skippedCount;
    private long totalProductsCount;
    private long totalListingsCount;
    private long outOfStockCount;
    private List<String> skippedReasons = new ArrayList<>();

    public CsvImportResultDTO() {}

    public int getTotalRowsRead() { return totalRowsRead; }
    public void setTotalRowsRead(int totalRowsRead) { this.totalRowsRead = totalRowsRead; }

    public int getImportedCount() { return importedCount; }
    public void setImportedCount(int importedCount) { this.importedCount = importedCount; }

    public int getUpdatedCount() { return updatedCount; }
    public void setUpdatedCount(int updatedCount) { this.updatedCount = updatedCount; }

    public int getSkippedCount() { return skippedCount; }
    public void setSkippedCount(int skippedCount) { this.skippedCount = skippedCount; }

    public long getTotalProductsCount() { return totalProductsCount; }
    public void setTotalProductsCount(long totalProductsCount) { this.totalProductsCount = totalProductsCount; }

    public long getTotalListingsCount() { return totalListingsCount; }
    public void setTotalListingsCount(long totalListingsCount) { this.totalListingsCount = totalListingsCount; }

    public long getOutOfStockCount() { return outOfStockCount; }
    public void setOutOfStockCount(long outOfStockCount) { this.outOfStockCount = outOfStockCount; }

    public List<String> getSkippedReasons() { return skippedReasons; }
    public void setSkippedReasons(List<String> skippedReasons) { this.skippedReasons = skippedReasons; }

    public void addSkippedReason(String reason) {
        if (this.skippedReasons == null) {
            this.skippedReasons = new ArrayList<>();
        }
        this.skippedReasons.add(reason);
    }
}
