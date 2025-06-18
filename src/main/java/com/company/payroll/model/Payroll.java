package com.company.payroll.model;

import java.time.LocalDate;
import java.util.List;

public class Payroll {
    private int id;
    private int driverId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private double gross;
    private double serviceFee;
    private double grossAfterServiceFee;
    private double fuel;
    private double grossAfterFuel;
    private double driverShare;
    private double companyShare;
    private double otherDeductions;
    private double net;
    private List<PayrollLine> payrollLines;

    // Getters and setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public double getGross() { return gross; }
    public void setGross(double gross) { this.gross = gross; }

    public double getServiceFee() { return serviceFee; }
    public void setServiceFee(double serviceFee) { this.serviceFee = serviceFee; }

    public double getGrossAfterServiceFee() { return grossAfterServiceFee; }
    public void setGrossAfterServiceFee(double grossAfterServiceFee) { this.grossAfterServiceFee = grossAfterServiceFee; }

    public double getFuel() { return fuel; }
    public void setFuel(double fuel) { this.fuel = fuel; }

    public double getGrossAfterFuel() { return grossAfterFuel; }
    public void setGrossAfterFuel(double grossAfterFuel) { this.grossAfterFuel = grossAfterFuel; }

    public double getDriverShare() { return driverShare; }
    public void setDriverShare(double driverShare) { this.driverShare = driverShare; }

    public double getCompanyShare() { return companyShare; }
    public void setCompanyShare(double companyShare) { this.companyShare = companyShare; }

    public double getOtherDeductions() { return otherDeductions; }
    public void setOtherDeductions(double otherDeductions) { this.otherDeductions = otherDeductions; }

    public double getNet() { return net; }
    public void setNet(double net) { this.net = net; }

    public List<PayrollLine> getPayrollLines() { return payrollLines; }
    public void setPayrollLines(List<PayrollLine> payrollLines) { this.payrollLines = payrollLines; }
}