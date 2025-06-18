package com.company.payroll.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CashAdvance {
    private int id;
    private int driverId;
    private LocalDate advanceDate; // for UI
    private LocalDate issuedDate;  // for DAO
    private BigDecimal amount;
    private BigDecimal weeklyRepayment;
    private String notes;
    private String status;
    private Integer assignedPayrollId;
    private LocalDateTime createdAt;

    public CashAdvance() {}

    public CashAdvance(int id, int driverId, LocalDate advanceDate, LocalDate issuedDate, BigDecimal amount, BigDecimal weeklyRepayment, String notes, String status, Integer assignedPayrollId, LocalDateTime createdAt) {
        this.id = id;
        this.driverId = driverId;
        this.advanceDate = advanceDate;
        this.issuedDate = issuedDate;
        this.amount = amount;
        this.weeklyRepayment = weeklyRepayment;
        this.notes = notes;
        this.status = status;
        this.assignedPayrollId = assignedPayrollId;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    // For UI compatibility
    public LocalDate getAdvanceDate() { return advanceDate != null ? advanceDate : issuedDate; }
    public void setAdvanceDate(LocalDate advanceDate) { this.advanceDate = advanceDate; this.issuedDate = advanceDate; }

    public LocalDate getIssuedDate() { return issuedDate != null ? issuedDate : advanceDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; this.advanceDate = issuedDate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

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