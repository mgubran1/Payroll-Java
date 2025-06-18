package com.company.payroll.model;

import java.time.LocalDate;
import java.util.Objects;

public class OtherDeduction {
    private int id;
    private int driverId;
    private LocalDate date;
    private String type;
    private double amount;
    private String notes;

    public OtherDeduction() {}

    public OtherDeduction(int id, int driverId, LocalDate date, String type, double amount, String notes) {
        this.id = id;
        this.driverId = driverId;
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "OtherDeduction{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", notes='" + notes + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OtherDeduction)) return false;
        OtherDeduction that = (OtherDeduction) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}