package com.company.payroll.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class Payroll {
    private int id;
    private int driverId;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private BigDecimal gross;
    private BigDecimal deductions;
    private BigDecimal net;
    private LocalDateTime createdAt;

    public Payroll() {}

    public Payroll(int id, int driverId, LocalDate weekStart, LocalDate weekEnd, BigDecimal gross,
                   BigDecimal deductions, BigDecimal net, LocalDateTime createdAt) {
        this.id = id;
        this.driverId = driverId;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.gross = gross;
        this.deductions = deductions;
        this.net = net;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }
    public LocalDate getWeekStart() { return weekStart; }
    public void setWeekStart(LocalDate weekStart) { this.weekStart = weekStart; }
    public LocalDate getWeekEnd() { return weekEnd; }
    public void setWeekEnd(LocalDate weekEnd) { this.weekEnd = weekEnd; }
    public BigDecimal getGross() { return gross; }
    public void setGross(BigDecimal gross) { this.gross = gross; }
    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }
    public BigDecimal getNet() { return net; }
    public void setNet(BigDecimal net) { this.net = net; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}