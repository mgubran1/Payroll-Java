package com.company.payroll.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MonthlyFee {
    private int id;
    private int driverId;
    private String feeType;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate endDate; // <-- Added for support of end dates
    private BigDecimal weeklyFee;
    private String notes;
    private LocalDateTime createdAt;

    public MonthlyFee() {}

    // Constructor with endDate included for convenience
    public MonthlyFee(int id, int driverId, String feeType, BigDecimal amount, LocalDate dueDate, LocalDate endDate, BigDecimal weeklyFee, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.driverId = driverId;
        this.feeType = feeType;
        this.amount = amount;
        this.dueDate = dueDate;
        this.endDate = endDate;
        this.weeklyFee = weeklyFee;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public String getFeeType() { return feeType; }
    public void setFeeType(String feeType) { this.feeType = feeType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getWeeklyFee() { return weeklyFee; }
    public void setWeeklyFee(BigDecimal weeklyFee) { this.weeklyFee = weeklyFee; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}