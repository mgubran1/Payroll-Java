package com.company.payroll.model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class CompanyIncome {
    private int id;
    private LocalDate date;
    private String customer;
    private BigDecimal amount;
    private String notes;
    private Integer payrollId;
    private String type;

    public CompanyIncome() {}

    public CompanyIncome(int id, LocalDate date, String customer, BigDecimal amount, String notes, Integer payrollId, String type) {
        this.id = id;
        this.date = date;
        this.customer = customer;
        this.amount = amount;
        this.notes = notes;
        this.payrollId = payrollId;
        this.type = type;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Integer getPayrollId() { return payrollId; }
    public void setPayrollId(Integer payrollId) { this.payrollId = payrollId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}