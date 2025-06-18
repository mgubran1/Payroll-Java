package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.Driver;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DriverDao {

    public DriverDao() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS drivers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL UNIQUE," +
                "truck_id TEXT," +
                "driver_percent REAL NOT NULL DEFAULT 0.0," +
                "fuel_discount_eligible INTEGER NOT NULL DEFAULT 0," +
                "company_service_fee_percent REAL NOT NULL DEFAULT 0.0," +
                "company_percent REAL NOT NULL DEFAULT 0.0," +
                "drive_type TEXT DEFAULT 'Owner Operator'," +
                "phone_number TEXT," +
                "cdl_expiry DATE," +
                "medical_expiry DATE," +
                "license_number TEXT," +
                "drivers_llc TEXT" +
                ")";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Driver> getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                drivers.add(toDriver(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    public Driver getDriverById(int id) {
        String sql = "SELECT * FROM drivers WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toDriver(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Driver getDriverByName(String name) {
        String sql = "SELECT * FROM drivers WHERE name = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toDriver(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addDriver(Driver driver) {
        String sql = "INSERT INTO drivers (name, truck_id, driver_percent, fuel_discount_eligible, company_service_fee_percent, company_percent, drive_type, phone_number, cdl_expiry, medical_expiry, license_number, drivers_llc) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, driver.getName());
            pstmt.setString(2, driver.getTruckId());
            pstmt.setDouble(3, driver.getDriverPercent());
            pstmt.setInt(4, driver.isFuelDiscountEligible() ? 1 : 0);
            pstmt.setDouble(5, driver.getCompanyServiceFeePercent());
            pstmt.setDouble(6, driver.getCompanyPercent());
            pstmt.setString(7, driver.getDriveType());
            pstmt.setString(8, driver.getPhoneNumber());
            pstmt.setString(9, driver.getCdlExpiry() != null ? driver.getCdlExpiry().toString() : null);
            pstmt.setString(10, driver.getMedicalExpiry() != null ? driver.getMedicalExpiry().toString() : null);
            pstmt.setString(11, driver.getLicenseNumber());
            pstmt.setString(12, driver.getDriversLLC());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDriver(Driver driver) {
        String sql = "UPDATE drivers SET name=?, truck_id=?, driver_percent=?, fuel_discount_eligible=?, company_service_fee_percent=?, company_percent=?, drive_type=?, phone_number=?, cdl_expiry=?, medical_expiry=?, license_number=?, drivers_llc=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, driver.getName());
            pstmt.setString(2, driver.getTruckId());
            pstmt.setDouble(3, driver.getDriverPercent());
            pstmt.setInt(4, driver.isFuelDiscountEligible() ? 1 : 0);
            pstmt.setDouble(5, driver.getCompanyServiceFeePercent());
            pstmt.setDouble(6, driver.getCompanyPercent());
            pstmt.setString(7, driver.getDriveType());
            pstmt.setString(8, driver.getPhoneNumber());
            pstmt.setString(9, driver.getCdlExpiry() != null ? driver.getCdlExpiry().toString() : null);
            pstmt.setString(10, driver.getMedicalExpiry() != null ? driver.getMedicalExpiry().toString() : null);
            pstmt.setString(11, driver.getLicenseNumber());
            pstmt.setString(12, driver.getDriversLLC());
            pstmt.setInt(13, driver.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDriver(int id) {
        String sql = "DELETE FROM drivers WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Driver toDriver(ResultSet rs) throws SQLException {
        Driver driver = new Driver();
        driver.setId(rs.getInt("id"));
        driver.setName(rs.getString("name"));
        driver.setTruckId(rs.getString("truck_id"));
        driver.setDriverPercent(rs.getDouble("driver_percent"));
        driver.setFuelDiscountEligible(rs.getInt("fuel_discount_eligible") == 1);
        driver.setCompanyServiceFeePercent(rs.getDouble("company_service_fee_percent"));
        driver.setCompanyPercent(rs.getDouble("company_percent"));
        driver.setDriveType(rs.getString("drive_type"));
        driver.setPhoneNumber(rs.getString("phone_number"));
        String cdlExpiryStr = rs.getString("cdl_expiry");
        driver.setCdlExpiry(cdlExpiryStr != null ? LocalDate.parse(cdlExpiryStr) : null);
        String medExpiryStr = rs.getString("medical_expiry");
        driver.setMedicalExpiry(medExpiryStr != null ? LocalDate.parse(medExpiryStr) : null);
        driver.setLicenseNumber(rs.getString("license_number"));
        driver.setDriversLLC(rs.getString("drivers_llc"));
        return driver;
    }
}