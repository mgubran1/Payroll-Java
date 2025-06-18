package com.company.payroll.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Trailer {
    private int id;
    private String trailerNumber;
    private String vin;
    private String plateNumber;
    private LocalDate plateExpiry;
    private LocalDate insuranceExpiry;
    private String make;
    private String model;
    private Integer year;
    private String notes;
    private boolean active;
    private LocalDateTime createdAt;

    public Trailer() {}

    public Trailer(int id, String trailerNumber, String vin, String plateNumber, LocalDate plateExpiry,
                   LocalDate insuranceExpiry, String make, String model, Integer year, String notes,
                   boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.trailerNumber = trailerNumber;
        this.vin = vin;
        this.plateNumber = plateNumber;
        this.plateExpiry = plateExpiry;
        this.insuranceExpiry = insuranceExpiry;
        this.make = make;
        this.model = model;
        this.year = year;
        this.notes = notes;
        this.active = active;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTrailerNumber() { return trailerNumber; }
    public void setTrailerNumber(String trailerNumber) { this.trailerNumber = trailerNumber; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public LocalDate getPlateExpiry() { return plateExpiry; }
    public void setPlateExpiry(LocalDate plateExpiry) { this.plateExpiry = plateExpiry; }
    public LocalDate getInsuranceExpiry() { return insuranceExpiry; }
    public void setInsuranceExpiry(LocalDate insuranceExpiry) { this.insuranceExpiry = insuranceExpiry; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}