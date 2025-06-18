package com.company.payroll.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OtherDeduction {
    private int id;
    private int payrollId;
    private int driverId;
    private LocalDate date;
    private BigDecimal amount;
    private String reason;
    private int reimbursed;
    private BigDecimal discAmt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPayrollId() { return payrollId; }
    public void setPayrollId(int payrollId) { this.payrollId = payrollId; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public int getReimbursed() { return reimbursed; }
    public void setReimbursed(int reimbursed) { this.reimbursed = reimbursed; }

    public BigDecimal getDiscAmt() { return discAmt; }
    public void setDiscAmt(BigDecimal discAmt) { this.discAmt = discAmt; }
}