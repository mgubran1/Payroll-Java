package com.company.payroll.model;

import java.io.Serializable;
import java.time.LocalDate;

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

    // Payroll/DAO fields
    private String truckId;
    private String driveType;
    private String phoneNumber;
    private LocalDate cdlExpiry;
    private LocalDate medicalExpiry;
    private String licenseNumber;
    private String driversLLC;

    public Driver() {}

    public Driver(int id, String name, String phone, String email, boolean active,
                  double companyServiceFeePercent, double driverPercent, double companyPercent,
                  boolean fuelDiscountEligible, String notes,
                  String truckId, String driveType, String phoneNumber,
                  LocalDate cdlExpiry, LocalDate medicalExpiry, String licenseNumber, String driversLLC) {
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
        this.truckId = truckId;
        this.driveType = driveType;
        this.phoneNumber = phoneNumber;
        this.cdlExpiry = cdlExpiry;
        this.medicalExpiry = medicalExpiry;
        this.licenseNumber = licenseNumber;
        this.driversLLC = driversLLC;
    }

    // Getters and setters for all fields

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public double getCompanyServiceFeePercent() { return companyServiceFeePercent; }
    public void setCompanyServiceFeePercent(double companyServiceFeePercent) { this.companyServiceFeePercent = companyServiceFeePercent; }

    public double getDriverPercent() { return driverPercent; }
    public void setDriverPercent(double driverPercent) { this.driverPercent = driverPercent; }

    public double getCompanyPercent() { return companyPercent; }
    public void setCompanyPercent(double companyPercent) { this.companyPercent = companyPercent; }

    public boolean isFuelDiscountEligible() { return fuelDiscountEligible; }
    public void setFuelDiscountEligible(boolean fuelDiscountEligible) { this.fuelDiscountEligible = fuelDiscountEligible; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getTruckId() { return truckId; }
    public void setTruckId(String truckId) { this.truckId = truckId; }

    public String getDriveType() { return driveType; }
    public void setDriveType(String driveType) { this.driveType = driveType; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getCdlExpiry() { return cdlExpiry; }
    public void setCdlExpiry(LocalDate cdlExpiry) { this.cdlExpiry = cdlExpiry; }

    public LocalDate getMedicalExpiry() { return medicalExpiry; }
    public void setMedicalExpiry(LocalDate medicalExpiry) { this.medicalExpiry = medicalExpiry; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getDriversLLC() { return driversLLC; }
    public void setDriversLLC(String driversLLC) { this.driversLLC = driversLLC; }

    @Override
    public String toString() {
        return name != null ? name : ("Driver #" + id);
    }
}