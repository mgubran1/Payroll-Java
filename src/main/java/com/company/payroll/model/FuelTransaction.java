package com.company.payroll.model;

import java.time.LocalDate;
import java.util.Objects;

public class FuelTransaction {
    private int id;
    private int driverId;
    private LocalDate tranDate;
    private String vendor;
    private double qty;
    private double amt;
    private String notes;

    public FuelTransaction() {}

    public FuelTransaction(int id, int driverId, LocalDate tranDate, String vendor, double qty, double amt, String notes) {
        this.id = id;
        this.driverId = driverId;
        this.tranDate = tranDate;
        this.vendor = vendor;
        this.qty = qty;
        this.amt = amt;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public LocalDate getTranDate() { return tranDate; }
    public void setTranDate(LocalDate tranDate) { this.tranDate = tranDate; }

    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }

    public double getQty() { return qty; }
    public void setQty(double qty) { this.qty = qty; }

    public double getAmt() { return amt; }
    public void setAmt(double amt) { this.amt = amt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "FuelTransaction{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", tranDate=" + tranDate +
                ", vendor='" + vendor + '\'' +
                ", qty=" + qty +
                ", amt=" + amt +
                ", notes='" + notes + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FuelTransaction)) return false;
        FuelTransaction that = (FuelTransaction) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}