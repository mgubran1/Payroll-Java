package com.company.payroll.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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