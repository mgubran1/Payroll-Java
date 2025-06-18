package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.MaintenanceRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class MaintenanceRecordDao {
    public List<MaintenanceRecord> getAllMaintenanceRecords() {
        List<MaintenanceRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_records";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                records.add(toMaintenanceRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public void addMaintenanceRecord(MaintenanceRecord mr) {
        String sql = "INSERT INTO maintenance_records (date, truck_id, trailer_number, category, amount, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(mr.getDate()));
            pstmt.setString(2, mr.getTruckId());
            pstmt.setString(3, mr.getTrailerNumber());
            pstmt.setString(4, mr.getCategory());
            pstmt.setBigDecimal(5, mr.getAmount());
            pstmt.setString(6, mr.getDescription());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMaintenanceRecord(MaintenanceRecord mr) {
        String sql = "UPDATE maintenance_records SET date=?, truck_id=?, trailer_number=?, category=?, amount=?, description=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(mr.getDate()));
            pstmt.setString(2, mr.getTruckId());
            pstmt.setString(3, mr.getTrailerNumber());
            pstmt.setString(4, mr.getCategory());
            pstmt.setBigDecimal(5, mr.getAmount());
            pstmt.setString(6, mr.getDescription());
            pstmt.setInt(7, mr.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMaintenanceRecord(int id) {
        String sql = "DELETE FROM maintenance_records WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private MaintenanceRecord toMaintenanceRecord(ResultSet rs) throws SQLException {
        MaintenanceRecord mr = new MaintenanceRecord();
        mr.setId(rs.getInt("id"));
        mr.setDate(rs.getDate("date").toLocalDate());
        mr.setTruckId(rs.getString("truck_id"));
        mr.setTrailerNumber(rs.getString("trailer_number"));
        mr.setCategory(rs.getString("category"));
        mr.setAmount(rs.getBigDecimal("amount"));
        mr.setDescription(rs.getString("description"));
        return mr;
    }
}