package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.CashAdvance;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CashAdvanceDao {
    public List<CashAdvance> getAllCashAdvances() {
        List<CashAdvance> advances = new ArrayList<>();
        String sql = "SELECT * FROM cash_advance";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                advances.add(toCashAdvance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return advances;
    }

    public List<CashAdvance> getCashAdvancesByDriver(int driverId) {
        List<CashAdvance> advances = new ArrayList<>();
        String sql = "SELECT * FROM cash_advance WHERE driver_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, driverId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                advances.add(toCashAdvance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return advances;
    }

    public List<CashAdvance> getCashAdvancesByPayroll(int payrollId) {
        List<CashAdvance> advances = new ArrayList<>();
        String sql = "SELECT * FROM cash_advance WHERE assigned_payroll_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payrollId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                advances.add(toCashAdvance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return advances;
    }

    public void addCashAdvance(CashAdvance ca) {
        String sql = "INSERT INTO cash_advance (driver_id, amount, issued_date, weekly_repayment, notes, status, assigned_payroll_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, ca.getDriverId());
            pstmt.setBigDecimal(2, ca.getAmount());
            pstmt.setDate(3, Date.valueOf(ca.getIssuedDate()));
            pstmt.setBigDecimal(4, ca.getWeeklyRepayment());
            pstmt.setString(5, ca.getNotes());
            pstmt.setString(6, ca.getStatus());
            if (ca.getAssignedPayrollId() == null) {
                pstmt.setNull(7, Types.INTEGER);
            } else {
                pstmt.setInt(7, ca.getAssignedPayrollId());
            }
            pstmt.setTimestamp(8, ca.getCreatedAt() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(ca.getCreatedAt()));
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ca.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCashAdvance(CashAdvance ca) {
        String sql = "UPDATE cash_advance SET driver_id=?, amount=?, issued_date=?, weekly_repayment=?, notes=?, status=?, assigned_payroll_id=?, created_at=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ca.getDriverId());
            pstmt.setBigDecimal(2, ca.getAmount());
            pstmt.setDate(3, Date.valueOf(ca.getIssuedDate()));
            pstmt.setBigDecimal(4, ca.getWeeklyRepayment());
            pstmt.setString(5, ca.getNotes());
            pstmt.setString(6, ca.getStatus());
            if (ca.getAssignedPayrollId() == null) {
                pstmt.setNull(7, Types.INTEGER);
            } else {
                pstmt.setInt(7, ca.getAssignedPayrollId());
            }
            pstmt.setTimestamp(8, ca.getCreatedAt() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(ca.getCreatedAt()));
            pstmt.setInt(9, ca.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCashAdvance(int id) {
        String sql = "DELETE FROM cash_advance WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private CashAdvance toCashAdvance(ResultSet rs) throws SQLException {
        CashAdvance ca = new CashAdvance();
        ca.setId(rs.getInt("id"));
        ca.setDriverId(rs.getInt("driver_id"));
        ca.setAmount(rs.getBigDecimal("amount"));
        ca.setIssuedDate(rs.getDate("issued_date").toLocalDate());
        ca.setWeeklyRepayment(rs.getBigDecimal("weekly_repayment"));
        ca.setNotes(rs.getString("notes"));
        ca.setStatus(rs.getString("status"));
        int payrollId = rs.getInt("assigned_payroll_id");
        ca.setAssignedPayrollId(rs.wasNull() ? null : payrollId);
        Timestamp ts = rs.getTimestamp("created_at");
        ca.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
        return ca;
    }
}