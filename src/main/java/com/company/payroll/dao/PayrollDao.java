package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.Payroll;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PayrollDao {
    public List<Payroll> getAllPayrolls() {
        List<Payroll> payrolls = new ArrayList<>();
        String sql = "SELECT * FROM payrolls";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                payrolls.add(toPayroll(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payrolls;
    }

    public void addPayroll(Payroll payroll) {
        String sql = "INSERT INTO payrolls (driver_id, week_start, week_end, gross, deductions, net, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payroll.getDriverId());
            pstmt.setDate(2, Date.valueOf(payroll.getWeekStart()));
            pstmt.setDate(3, Date.valueOf(payroll.getWeekEnd()));
            pstmt.setBigDecimal(4, payroll.getGross());
            pstmt.setBigDecimal(5, payroll.getDeductions());
            pstmt.setBigDecimal(6, payroll.getNet());
            pstmt.setTimestamp(7, payroll.getCreatedAt() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(payroll.getCreatedAt()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePayroll(Payroll payroll) {
        String sql = "UPDATE payrolls SET driver_id=?, week_start=?, week_end=?, gross=?, deductions=?, net=?, created_at=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payroll.getDriverId());
            pstmt.setDate(2, Date.valueOf(payroll.getWeekStart()));
            pstmt.setDate(3, Date.valueOf(payroll.getWeekEnd()));
            pstmt.setBigDecimal(4, payroll.getGross());
            pstmt.setBigDecimal(5, payroll.getDeductions());
            pstmt.setBigDecimal(6, payroll.getNet());
            pstmt.setTimestamp(7, payroll.getCreatedAt() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(payroll.getCreatedAt()));
            pstmt.setInt(8, payroll.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePayroll(int id) {
        String sql = "DELETE FROM payrolls WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Payroll toPayroll(ResultSet rs) throws SQLException {
        Payroll p = new Payroll();
        p.setId(rs.getInt("id"));
        p.setDriverId(rs.getInt("driver_id"));
        p.setWeekStart(rs.getDate("week_start").toLocalDate());
        p.setWeekEnd(rs.getDate("week_end").toLocalDate());
        p.setGross(rs.getBigDecimal("gross"));
        p.setDeductions(rs.getBigDecimal("deductions"));
        p.setNet(rs.getBigDecimal("net"));
        Timestamp ts = rs.getTimestamp("created_at");
        p.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
        return p;
    }
}