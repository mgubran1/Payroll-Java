package com.company.payroll.model;

import java.time.LocalDate;
import java.util.Objects;

public class Load {
    private int id;
    private String loadNumber;
    private String customer;
    private String pickupLocation;
    private String dropLocation;
    private int driverId;
    private LocalDate deliveredDate;
    private double grossAmount;
    private double driverPercent;
    private String description;
    private boolean paid; // true if paid, false if unpaid

    // No-argument constructor
    public Load() {}

    // All-argument constructor
    public Load(int id, String loadNumber, String customer, String pickupLocation, String dropLocation,
                int driverId, LocalDate deliveredDate, double grossAmount, double driverPercent,
                String description, boolean paid) {
        this.id = id;
        this.loadNumber = loadNumber;
        this.customer = customer;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.driverId = driverId;
        this.deliveredDate = deliveredDate;
        this.grossAmount = grossAmount;
        this.driverPercent = driverPercent;
        this.description = description;
        this.paid = paid;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLoadNumber() { return loadNumber; }
    public void setLoadNumber(String loadNumber) { this.loadNumber = loadNumber; }

    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getDropLocation() { return dropLocation; }
    public void setDropLocation(String dropLocation) { this.dropLocation = dropLocation; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public LocalDate getDeliveredDate() { return deliveredDate; }
    public void setDeliveredDate(LocalDate deliveredDate) { this.deliveredDate = deliveredDate; }

    public double getGrossAmount() { return grossAmount; }
    public void setGrossAmount(double grossAmount) { this.grossAmount = grossAmount; }

    public double getDriverPercent() { return driverPercent; }
    public void setDriverPercent(double driverPercent) { this.driverPercent = driverPercent; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    // toString for easy debugging/logging
    @Override
    public String toString() {
        return "Load{" +
                "id=" + id +
                ", loadNumber='" + loadNumber + '\'' +
                ", customer='" + customer + '\'' +
                ", pickupLocation='" + pickupLocation + '\'' +
                ", dropLocation='" + dropLocation + '\'' +
                ", driverId=" + driverId +
                ", deliveredDate=" + deliveredDate +
                ", grossAmount=" + grossAmount +
                ", driverPercent=" + driverPercent +
                ", description='" + description + '\'' +
                ", paid=" + paid +
                '}';
    }

    // equals and hashCode (optional, but good for use in sets or lists)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Load)) return false;
        Load load = (Load) o;
        return id == load.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}