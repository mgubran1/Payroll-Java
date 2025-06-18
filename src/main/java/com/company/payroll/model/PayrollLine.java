package com.company.payroll.model;

public class PayrollLine {
    private int id;
    private int payrollId;
    private String type; // "Load", "Fuel", "MonthlyFee", "CashAdvance", "OtherDeduction"
    private int referenceId; // ID to the actual line item in its table
    private double amount;

    public PayrollLine() {}

    public PayrollLine(int id, int payrollId, String type, int referenceId, double amount) {
        this.id = id;
        this.payrollId = payrollId;
        this.type = type;
        this.referenceId = referenceId;
        this.amount = amount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPayrollId() { return payrollId; }
    public void setPayrollId(int payrollId) { this.payrollId = payrollId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getReferenceId() { return referenceId; }
    public void setReferenceId(int referenceId) { this.referenceId = referenceId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}