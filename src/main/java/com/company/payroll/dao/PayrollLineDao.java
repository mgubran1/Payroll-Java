package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.PayrollLine;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PayrollLineDao {
    public List<PayrollLine> getAllPayrollLines() {
        List<PayrollLine> lines = new ArrayList<>();
        String sql = "SELECT * FROM payroll_lines";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lines.add(toPayrollLine(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public void addPayrollLine(PayrollLine line) {
        String sql = "INSERT INTO payroll_lines (payroll_id, load_id, fuel_transaction_id, description, amount) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, line.getPayrollId());
            if (line.getLoadId() == null) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, line.getLoadId());
            }
            if (line.getFuelTransactionId() == null) {
                pstmt.setNull(3, Types.INTEGER);
            } else {
                pstmt.setInt(3, line.getFuelTransactionId());
            }
            pstmt.setString(4, line.getDescription());
            pstmt.setBigDecimal(5, line.getAmount());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePayrollLine(PayrollLine line) {
        String sql = "UPDATE payroll_lines SET payroll_id=?, load_id=?, fuel_transaction_id=?, description=?, amount=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, line.getPayrollId());
            if (line.getLoadId() == null) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, line.getLoadId());
            }
            if (line.getFuelTransactionId() == null) {
                pstmt.setNull(3, Types.INTEGER);
            } else {
                pstmt.setInt(3, line.getFuelTransactionId());
            }
            pstmt.setString(4, line.getDescription());
            pstmt.setBigDecimal(5, line.getAmount());
            pstmt.setInt(6, line.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePayrollLine(int id) {
        String sql = "DELETE FROM payroll_lines WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private PayrollLine toPayrollLine(ResultSet rs) throws SQLException {
        PayrollLine l = new PayrollLine();
        l.setId(rs.getInt("id"));
        l.setPayrollId(rs.getInt("payroll_id"));
        int loadId = rs.getInt("load_id");
        l.setLoadId(rs.wasNull() ? null : loadId);
        int fuelTransId = rs.getInt("fuel_transaction_id");
        l.setFuelTransactionId(rs.wasNull() ? null : fuelTransId);
        l.setDescription(rs.getString("description"));
        l.setAmount(rs.getBigDecimal("amount"));
        return l;
    }
}