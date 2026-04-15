package com.example.casemanagement.dto;

public class AdminStatsDTO {

    private String name;
    private long total;
    private long handled;
    private long pending;

    public AdminStatsDTO(String name, long total, long handled, long pending) {
        this.name = name;
        this.total = total;
        this.handled = handled;
        this.pending = pending;
    }

    public String getName() { return name; }
    public long getTotal() { return total; }
    public long getHandled() { return handled; }
    public long getPending() { return pending; }
}
