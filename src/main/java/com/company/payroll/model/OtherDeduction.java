package com.company.payroll.model;

import java.time.LocalDate;
import java.util.Objects;

public class OtherDeduction {
    private int id;
    private int driverId;
    private int payrollId;
    private LocalDate date;
    private String type;
    private String reason;
    private double amount;
    private double discAmt;
    private int reimbursed;
    private String notes;

    public OtherDeduction() {}

    public OtherDeduction(int id, int driverId, int payrollId, LocalDate date, String type, String reason, double amount, double discAmt, int reimbursed, String notes) {
        this.id = id;
        this.driverId = driverId;
        this.payrollId = payrollId;
        this.date = date;
        this.type = type;
        this.reason = reason;
        this.amount = amount;
        this.discAmt = discAmt;
        this.reimbursed = reimbursed;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public int getPayrollId() { return payrollId; }
    public void setPayrollId(int payrollId) { this.payrollId = payrollId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getDiscAmt() { return discAmt; }
    public void setDiscAmt(double discAmt) { this.discAmt = discAmt; }

    public int getReimbursed() { return reimbursed; }
    public void setReimbursed(int reimbursed) { this.reimbursed = reimbursed; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "OtherDeduction{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", payrollId=" + payrollId +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", reason='" + reason + '\'' +
                ", amount=" + amount +
                ", discAmt=" + discAmt +
                ", reimbursed=" + reimbursed +
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