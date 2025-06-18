package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.PayrollLine;

import java.sql.*;
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
        String sql = "INSERT INTO payroll_lines (payroll_id, type, reference_id, amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, line.getPayrollId());
            pstmt.setString(2, line.getType());
            pstmt.setInt(3, line.getReferenceId());
            pstmt.setDouble(4, line.getAmount());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    line.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private PayrollLine toPayrollLine(ResultSet rs) throws SQLException {
        PayrollLine pl = new PayrollLine();
        pl.setId(rs.getInt("id"));
        pl.setPayrollId(rs.getInt("payroll_id"));
        pl.setType(rs.getString("type"));
        pl.setReferenceId(rs.getInt("reference_id"));
        pl.setAmount(rs.getDouble("amount"));
        return pl;
    }
    // Add update, delete, and getById methods as needed.
}