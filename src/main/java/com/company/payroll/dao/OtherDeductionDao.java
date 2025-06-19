package com.company.payroll.dao;

import com.company.payroll.model.OtherDeduction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OtherDeductionDao {
    private static final Logger logger = LoggerFactory.getLogger(OtherDeductionDao.class);
    private final String url = "jdbc:sqlite:payroll.db";

    public OtherDeductionDao() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS other_deductions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "driverId INTEGER," +
                "payrollId INTEGER," +
                "date TEXT," +
                "type TEXT," +
                "reason TEXT," +
                "amount REAL," +
                "discAmt REAL," +
                "reimbursed INTEGER," +
                "notes TEXT" +
                ")";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Table checked/created.");
        } catch (SQLException ex) {
            logger.error("Table creation error", ex);
        }
    }

    public void addDeduction(OtherDeduction d) {
        String sql = "INSERT INTO other_deductions (driverId, payrollId, date, type, reason, amount, discAmt, reimbursed, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d.getDriverId());
            ps.setInt(2, d.getPayrollId());
            ps.setString(3, d.getDate() != null ? d.getDate().toString() : null);
            ps.setString(4, d.getType());
            ps.setString(5, d.getReason());
            ps.setDouble(6, d.getAmount());
            ps.setDouble(7, d.getDiscAmt());
            ps.setInt(8, d.getReimbursed());
            ps.setString(9, d.getNotes());
            ps.executeUpdate();
            logger.info("Added deduction for driverId {} payrollId {}", d.getDriverId(), d.getPayrollId());
        } catch (SQLException ex) {
            logger.error("Add deduction error", ex);
        }
    }

    public void updateDeduction(OtherDeduction d) {
        String sql = "UPDATE other_deductions SET driverId=?, payrollId=?, date=?, type=?, reason=?, amount=?, discAmt=?, reimbursed=?, notes=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d.getDriverId());
            ps.setInt(2, d.getPayrollId());
            ps.setString(3, d.getDate() != null ? d.getDate().toString() : null);
            ps.setString(4, d.getType());
            ps.setString(5, d.getReason());
            ps.setDouble(6, d.getAmount());
            ps.setDouble(7, d.getDiscAmt());
            ps.setInt(8, d.getReimbursed());
            ps.setString(9, d.getNotes());
            ps.setInt(10, d.getId());
            ps.executeUpdate();
            logger.info("Updated deduction id {}", d.getId());
        } catch (SQLException ex) {
            logger.error("Update deduction error", ex);
        }
    }

    public void deleteDeduction(int id) {
        String sql = "DELETE FROM other_deductions WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            logger.info("Deleted deduction id {}", id);
        } catch (SQLException ex) {
            logger.error("Delete deduction error", ex);
        }
    }

    public List<OtherDeduction> getAllDeductions() {
        List<OtherDeduction> list = new ArrayList<>();
        String sql = "SELECT * FROM other_deductions";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(readEntry(rs));
            }
        } catch (SQLException ex) {
            logger.error("Get all deductions error", ex);
        }
        return list;
    }

    public List<OtherDeduction> getDeductionsByDriver(int driverId) {
        List<OtherDeduction> list = new ArrayList<>();
        String sql = "SELECT * FROM other_deductions WHERE driverId = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readEntry(rs));
            }
        } catch (SQLException ex) {
            logger.error("Get deductions by driver error", ex);
        }
        return list;
    }

    private OtherDeduction readEntry(ResultSet rs) throws SQLException {
        OtherDeduction d = new OtherDeduction();
        d.setId(rs.getInt("id"));
        d.setDriverId(rs.getInt("driverId"));
        d.setPayrollId(rs.getInt("payrollId"));
        String dateStr = rs.getString("date");
        d.setDate(dateStr != null ? LocalDate.parse(dateStr) : null);
        d.setType(rs.getString("type"));
        d.setReason(rs.getString("reason"));
        d.setAmount(rs.getDouble("amount"));
        d.setDiscAmt(rs.getDouble("discAmt"));
        d.setReimbursed(rs.getInt("reimbursed"));
        d.setNotes(rs.getString("notes"));
        return d;
    }

    // ----------- ADDED METHOD -----------
    public List<OtherDeduction> getOtherDeductionsByDriverAndDateRange(int driverId, LocalDate from, LocalDate to) {
        List<OtherDeduction> list = new ArrayList<>();
        String sql = "SELECT * FROM other_deductions WHERE driverId = ? AND date >= ? AND date <= ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, driverId);
            pstmt.setString(2, from.toString());
            pstmt.setString(3, to.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(readEntry(rs));
            }
        } catch (SQLException ex) {
            logger.error("Get by driver/date error", ex);
        }
        return list;
    }
}