package com.company.payroll.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class CashAdvance {
    private int id;
    private int driverId;
    private BigDecimal amount;
    private LocalDate issuedDate;
    private BigDecimal weeklyRepayment;
    private String notes;
    private String status;
    private Integer assignedPayrollId;
    private LocalDateTime createdAt;

    public CashAdvance() {}

    public CashAdvance(int id, int driverId, BigDecimal amount, LocalDate issuedDate, BigDecimal weeklyRepayment,
                       String notes, String status, Integer assignedPayrollId, LocalDateTime createdAt) {
        this.id = id;
        this.driverId = driverId;
        this.amount = amount;
        this.issuedDate = issuedDate;
        this.weeklyRepayment = weeklyRepayment;
        this.notes = notes;
        this.status = status;
        this.assignedPayrollId = assignedPayrollId;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }
    public BigDecimal getWeeklyRepayment() { return weeklyRepayment; }
    public void setWeeklyRepayment(BigDecimal weeklyRepayment) { this.weeklyRepayment = weeklyRepayment; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getAssignedPayrollId() { return assignedPayrollId; }
    public void setAssignedPayrollId(Integer assignedPayrollId) { this.assignedPayrollId = assignedPayrollId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}