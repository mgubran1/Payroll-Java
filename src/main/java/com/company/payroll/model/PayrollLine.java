package com.company.payroll.model;

import java.math.BigDecimal;

public class PayrollLine {
    private int id;
    private int payrollId;
    private Integer loadId;
    private Integer fuelTransactionId;
    private String description;
    private BigDecimal amount;

    public PayrollLine() {}

    public PayrollLine(int id, int payrollId, Integer loadId, Integer fuelTransactionId, String description, BigDecimal amount) {
        this.id = id;
        this.payrollId = payrollId;
        this.loadId = loadId;
        this.fuelTransactionId = fuelTransactionId;
        this.description = description;
        this.amount = amount;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPayrollId() { return payrollId; }
    public void setPayrollId(int payrollId) { this.payrollId = payrollId; }
    public Integer getLoadId() { return loadId; }
    public void setLoadId(Integer loadId) { this.loadId = loadId; }
    public Integer getFuelTransactionId() { return fuelTransactionId; }
    public void setFuelTransactionId(Integer fuelTransactionId) { this.fuelTransactionId = fuelTransactionId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}