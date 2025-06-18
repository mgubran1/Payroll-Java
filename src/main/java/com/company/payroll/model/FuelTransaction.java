package com.company.payroll.model;

public class FuelTransaction {
    private String cardNumber;
    private String tranDate;
    private String tranTime;
    private String invoice;
    private String unit;
    private String driverName;
    private String odometer;
    private String locationName;
    private String city;
    private String stateProv;
    private String fees;
    private String item;
    private String unitPrice;
    private String discPPU;
    private String discCost;
    private String qty;
    private String discAmt;
    private String discType;
    private String amt;
    private String db;
    private String currency;

    // Add driverId and vendor, notes for UI compatibility
    private Integer driverId;
    private String vendor;
    private String notes;

    public Integer getDriverId() { return driverId; }
    public void setDriverId(Integer driverId) { this.driverId = driverId; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // ... all your existing getters and setters ...
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getTranDate() { return tranDate; }
    public void setTranDate(String tranDate) { this.tranDate = tranDate; }
    public String getTranTime() { return tranTime; }
    public void setTranTime(String tranTime) { this.tranTime = tranTime; }
    public String getInvoice() { return invoice; }
    public void setInvoice(String invoice) { this.invoice = invoice; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getOdometer() { return odometer; }
    public void setOdometer(String odometer) { this.odometer = odometer; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getStateProv() { return stateProv; }
    public void setStateProv(String stateProv) { this.stateProv = stateProv; }
    public String getFees() { return fees; }
    public void setFees(String fees) { this.fees = fees; }
    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }
    public String getUnitPrice() { return unitPrice; }
    public void setUnitPrice(String unitPrice) { this.unitPrice = unitPrice; }
    public String getDiscPPU() { return discPPU; }
    public void setDiscPPU(String discPPU) { this.discPPU = discPPU; }
    public String getDiscCost() { return discCost; }
    public void setDiscCost(String discCost) { this.discCost = discCost; }
    public String getQty() { return qty; }
    public void setQty(String qty) { this.qty = qty; }
    public String getDiscAmt() { return discAmt; }
    public void setDiscAmt(String discAmt) { this.discAmt = discAmt; }
    public String getDiscType() { return discType; }
    public void setDiscType(String discType) { this.discType = discType; }
    public String getAmt() { return amt; }
    public void setAmt(String amt) { this.amt = amt; }
    public String getDb() { return db; }
    public void setDb(String db) { this.db = db; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}