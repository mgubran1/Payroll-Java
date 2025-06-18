package com.company.payroll.model;

import java.io.Serializable;

public class Driver implements Serializable {
    private int id;
    private String name;
    private String phone;
    private String email;
    private boolean active;
    private double companyServiceFeePercent;
    private double driverPercent;
    private double companyPercent;
    private boolean fuelDiscountEligible;
    private String notes;

    public Driver() {}

    public Driver(int id, String name, String phone, String email, boolean active,
                  double companyServiceFeePercent, double driverPercent, double companyPercent,
                  boolean fuelDiscountEligible, String notes) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.active = active;
        this.companyServiceFeePercent = companyServiceFeePercent;
        this.driverPercent = driverPercent;
        this.companyPercent = companyPercent;
        this.fuelDiscountEligible = fuelDiscountEligible;
        this.notes = notes;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getCompanyServiceFeePercent() {
        return companyServiceFeePercent;
    }

    public void setCompanyServiceFeePercent(double companyServiceFeePercent) {
        this.companyServiceFeePercent = companyServiceFeePercent;
    }

    public double getDriverPercent() {
        return driverPercent;
    }

    public void setDriverPercent(double driverPercent) {
        this.driverPercent = driverPercent;
    }

    public double getCompanyPercent() {
        return companyPercent;
    }

    public void setCompanyPercent(double companyPercent) {
        this.companyPercent = companyPercent;
    }

    public boolean isFuelDiscountEligible() {
        return fuelDiscountEligible;
    }

    public void setFuelDiscountEligible(boolean fuelDiscountEligible) {
        this.fuelDiscountEligible = fuelDiscountEligible;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Robust toString for ComboBox and display
    @Override
    public String toString() {
        // You may format this differently if you want (e.g. include phone, etc)
        return name != null ? name : ("Driver #" + id);
    }
}