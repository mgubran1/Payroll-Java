package com.company.payroll.model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class BusinessExpense {
    private int id;
    private LocalDate date;
    private String category;
    private BigDecimal amount;
    private String description;

    public BusinessExpense() {}

    public BusinessExpense(int id, LocalDate date, String category, BigDecimal amount, String description) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}