package com.company.payroll.model;

import java.time.LocalDate;
import java.util.Objects;

public class CashAdvance {
    private int id;
    private int driverId;
    private LocalDate advanceDate;
    private double amount;
    private String notes;

    public CashAdvance() {}

    public CashAdvance(int id, int driverId, LocalDate advanceDate, double amount, String notes) {
        this.id = id;
        this.driverId = driverId;
        this.advanceDate = advanceDate;
        this.amount = amount;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public LocalDate getAdvanceDate() { return advanceDate; }
    public void setAdvanceDate(LocalDate advanceDate) { this.advanceDate = advanceDate; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "CashAdvance{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", advanceDate=" + advanceDate +
                ", amount=" + amount +
                ", notes='" + notes + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CashAdvance)) return false;
        CashAdvance that = (CashAdvance) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}