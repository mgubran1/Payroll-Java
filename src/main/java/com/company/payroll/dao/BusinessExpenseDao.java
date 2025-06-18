package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.BusinessExpense;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class BusinessExpenseDao {
    public List<BusinessExpense> getAllBusinessExpenses() {
        List<BusinessExpense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM business_expenses";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                expenses.add(toBusinessExpense(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public void addBusinessExpense(BusinessExpense be) {
        String sql = "INSERT INTO business_expenses (date, category, amount, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(be.getDate()));
            pstmt.setString(2, be.getCategory());
            pstmt.setBigDecimal(3, be.getAmount());
            pstmt.setString(4, be.getDescription());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBusinessExpense(BusinessExpense be) {
        String sql = "UPDATE business_expenses SET date=?, category=?, amount=?, description=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(be.getDate()));
            pstmt.setString(2, be.getCategory());
            pstmt.setBigDecimal(3, be.getAmount());
            pstmt.setString(4, be.getDescription());
            pstmt.setInt(5, be.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBusinessExpense(int id) {
        String sql = "DELETE FROM business_expenses WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private BusinessExpense toBusinessExpense(ResultSet rs) throws SQLException {
        BusinessExpense be = new BusinessExpense();
        be.setId(rs.getInt("id"));
        be.setDate(rs.getDate("date").toLocalDate());
        be.setCategory(rs.getString("category"));
        be.setAmount(rs.getBigDecimal("amount"));
        be.setDescription(rs.getString("description"));
        return be;
    }
}