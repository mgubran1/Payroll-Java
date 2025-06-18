package com.company.payroll.model;

import java.time.LocalDate;

public class Driver {
    private int id;
    private String name;
    private String truckId;
    private double driverPercent;
    private boolean fuelDiscountEligible;
    private double companyServiceFeePercent;
    private double companyPercent;
    private String driveType;
    private String phoneNumber;
    private LocalDate cdlExpiry;
    private LocalDate medicalExpiry;
    private String licenseNumber;
    private String driversLLC;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTruckId() { return truckId; }
    public void setTruckId(String truckId) { this.truckId = truckId; }

    public double getDriverPercent() { return driverPercent; }
    public void setDriverPercent(double driverPercent) { this.driverPercent = driverPercent; }

    public boolean isFuelDiscountEligible() { return fuelDiscountEligible; }
    public void setFuelDiscountEligible(boolean fuelDiscountEligible) { this.fuelDiscountEligible = fuelDiscountEligible; }

    public double getCompanyServiceFeePercent() { return companyServiceFeePercent; }
    public void setCompanyServiceFeePercent(double companyServiceFeePercent) { this.companyServiceFeePercent = companyServiceFeePercent; }

    public double getCompanyPercent() { return companyPercent; }
    public void setCompanyPercent(double companyPercent) { this.companyPercent = companyPercent; }

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
}