package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.OtherDeduction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OtherDeductionDao {
    public List<OtherDeduction> getAllOtherDeductions() {
        List<OtherDeduction> deductions = new ArrayList<>();
        String sql = "SELECT * FROM other_deductions";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                deductions.add(toOtherDeduction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deductions;
    }

    public List<OtherDeduction> getOtherDeductionsByDriver(int driverId) {
        List<OtherDeduction> deductions = new ArrayList<>();
        String sql = "SELECT * FROM other_deductions WHERE driver_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, driverId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                deductions.add(toOtherDeduction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deductions;
    }

    public List<OtherDeduction> getOtherDeductionsByPayroll(int payrollId) {
        List<OtherDeduction> deductions = new ArrayList<>();
        String sql = "SELECT * FROM other_deductions WHERE payroll_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payrollId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                deductions.add(toOtherDeduction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deductions;
    }

    public void addOtherDeduction(OtherDeduction d) {
        String sql = "INSERT INTO other_deductions (payroll_id, driver_id, date, amount, reason, reimbursed, disc_amt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, d.getPayrollId());
            pstmt.setInt(2, d.getDriverId());
            pstmt.setDate(3, Date.valueOf(d.getDate()));
            pstmt.setBigDecimal(4, d.getAmount());
            pstmt.setString(5, d.getReason());
            pstmt.setInt(6, d.getReimbursed());
            pstmt.setBigDecimal(7, d.getDiscAmt());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    d.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOtherDeduction(OtherDeduction d) {
        String sql = "UPDATE other_deductions SET payroll_id=?, driver_id=?, date=?, amount=?, reason=?, reimbursed=?, disc_amt=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, d.getPayrollId());
            pstmt.setInt(2, d.getDriverId());
            pstmt.setDate(3, Date.valueOf(d.getDate()));
            pstmt.setBigDecimal(4, d.getAmount());
            pstmt.setString(5, d.getReason());
            pstmt.setInt(6, d.getReimbursed());
            pstmt.setBigDecimal(7, d.getDiscAmt());
            pstmt.setInt(8, d.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOtherDeduction(int id) {
        String sql = "DELETE FROM other_deductions WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private OtherDeduction toOtherDeduction(ResultSet rs) throws SQLException {
        OtherDeduction d = new OtherDeduction();
        d.setId(rs.getInt("id"));
        d.setPayrollId(rs.getInt("payroll_id"));
        d.setDriverId(rs.getInt("driver_id"));
        d.setDate(rs.getDate("date").toLocalDate());
        d.setAmount(rs.getBigDecimal("amount"));
        d.setReason(rs.getString("reason"));
        d.setReimbursed(rs.getInt("reimbursed"));
        d.setDiscAmt(rs.getBigDecimal("disc_amt"));
        return d;
    }
}