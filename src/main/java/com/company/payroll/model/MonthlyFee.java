package com.company.payroll.model;

import java.time.LocalDate;
import java.util.Objects;

public class MonthlyFee {
    private int id;
    private int driverId;
    private LocalDate dueDate;
    private String feeType;
    private double amount;
    private double weeklyFee;
    private String notes;

    public MonthlyFee() {}

    public MonthlyFee(int id, int driverId, LocalDate dueDate, String feeType, double amount, double weeklyFee, String notes) {
        this.id = id;
        this.driverId = driverId;
        this.dueDate = dueDate;
        this.feeType = feeType;
        this.amount = amount;
        this.weeklyFee = weeklyFee;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getFeeType() { return feeType; }
    public void setFeeType(String feeType) { this.feeType = feeType; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getWeeklyFee() { return weeklyFee; }
    public void setWeeklyFee(double weeklyFee) { this.weeklyFee = weeklyFee; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "MonthlyFee{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", dueDate=" + dueDate +
                ", feeType='" + feeType + '\'' +
                ", amount=" + amount +
                ", weeklyFee=" + weeklyFee +
                ", notes='" + notes + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonthlyFee)) return false;
        MonthlyFee that = (MonthlyFee) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}