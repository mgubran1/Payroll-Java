package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.MonthlyFee;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class MonthlyFeeDao {
    public List<MonthlyFee> getAllMonthlyFees() {
        List<MonthlyFee> fees = new ArrayList<>();
        String sql = "SELECT * FROM monthly_fees";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fees.add(toMonthlyFee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fees;
    }

    public void addMonthlyFee(MonthlyFee mf) {
        String sql = "INSERT INTO monthly_fees (driver_id, fee_type, amount, due_date, weekly_fee, notes, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mf.getDriverId());
            pstmt.setString(2, mf.getFeeType());
            pstmt.setBigDecimal(3, mf.getAmount());
            pstmt.setDate(4, Date.valueOf(mf.getDueDate()));
            pstmt.setBigDecimal(5, mf.getWeeklyFee());
            pstmt.setString(6, mf.getNotes());
            pstmt.setTimestamp(7, mf.getCreatedAt() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(mf.getCreatedAt()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMonthlyFee(MonthlyFee mf) {
        String sql = "UPDATE monthly_fees SET driver_id=?, fee_type=?, amount=?, due_date=?, weekly_fee=?, notes=?, created_at=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mf.getDriverId());
            pstmt.setString(2, mf.getFeeType());
            pstmt.setBigDecimal(3, mf.getAmount());
            pstmt.setDate(4, Date.valueOf(mf.getDueDate()));
            pstmt.setBigDecimal(5, mf.getWeeklyFee());
            pstmt.setString(6, mf.getNotes());
            pstmt.setTimestamp(7, mf.getCreatedAt() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(mf.getCreatedAt()));
            pstmt.setInt(8, mf.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMonthlyFee(int id) {
        String sql = "DELETE FROM monthly_fees WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private MonthlyFee toMonthlyFee(ResultSet rs) throws SQLException {
        MonthlyFee mf = new MonthlyFee();
        mf.setId(rs.getInt("id"));
        mf.setDriverId(rs.getInt("driver_id"));
        mf.setFeeType(rs.getString("fee_type"));
        mf.setAmount(rs.getBigDecimal("amount"));
        mf.setDueDate(rs.getDate("due_date").toLocalDate());
        mf.setWeeklyFee(rs.getBigDecimal("weekly_fee"));
        mf.setNotes(rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("created_at");
        mf.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
        return mf;
    }
}