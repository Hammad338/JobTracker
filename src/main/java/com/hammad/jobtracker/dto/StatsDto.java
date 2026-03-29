package com.hammad.jobtracker.dto;

import java.util.Map;

public class StatsDto {

    private long total;
    private long activeCount;
    private long offerCount;
    private long rejectedCount;
    private Map<String, Long> byStatus;

    public StatsDto(long total, long activeCount, long offerCount, long rejectedCount, Map<String, Long> byStatus) {
        this.total = total;
        this.activeCount = activeCount;
        this.offerCount = offerCount;
        this.rejectedCount = rejectedCount;
        this.byStatus = byStatus;
    }

    public long getTotal() { return total; }
    public long getActiveCount() { return activeCount; }
    public long getOfferCount() { return offerCount; }
    public long getRejectedCount() { return rejectedCount; }
    public Map<String, Long> getByStatus() { return byStatus; }
}
