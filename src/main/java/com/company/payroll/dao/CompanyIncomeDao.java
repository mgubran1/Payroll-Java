package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.CompanyIncome;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class CompanyIncomeDao {
    public List<CompanyIncome> getAllCompanyIncomes() {
        List<CompanyIncome> incomes = new ArrayList<>();
        String sql = "SELECT * FROM company_income";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                incomes.add(toCompanyIncome(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomes;
    }

    public void addCompanyIncome(CompanyIncome ci) {
        String sql = "INSERT INTO company_income (date, customer, amount, notes, payroll_id, type) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(ci.getDate()));
            pstmt.setString(2, ci.getCustomer());
            pstmt.setBigDecimal(3, ci.getAmount());
            pstmt.setString(4, ci.getNotes());
            if (ci.getPayrollId() == null) {
                pstmt.setNull(5, Types.INTEGER);
            } else {
                pstmt.setInt(5, ci.getPayrollId());
            }
            pstmt.setString(6, ci.getType());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCompanyIncome(CompanyIncome ci) {
        String sql = "UPDATE company_income SET date=?, customer=?, amount=?, notes=?, payroll_id=?, type=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(ci.getDate()));
            pstmt.setString(2, ci.getCustomer());
            pstmt.setBigDecimal(3, ci.getAmount());
            pstmt.setString(4, ci.getNotes());
            if (ci.getPayrollId() == null) {
                pstmt.setNull(5, Types.INTEGER);
            } else {
                pstmt.setInt(5, ci.getPayrollId());
            }
            pstmt.setString(6, ci.getType());
            pstmt.setInt(7, ci.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCompanyIncome(int id) {
        String sql = "DELETE FROM company_income WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private CompanyIncome toCompanyIncome(ResultSet rs) throws SQLException {
        CompanyIncome ci = new CompanyIncome();
        ci.setId(rs.getInt("id"));
        ci.setDate(rs.getDate("date").toLocalDate());
        ci.setCustomer(rs.getString("customer"));
        ci.setAmount(rs.getBigDecimal("amount"));
        ci.setNotes(rs.getString("notes"));
        int payrollId = rs.getInt("payroll_id");
        ci.setPayrollId(rs.wasNull() ? null : payrollId);
        ci.setType(rs.getString("type"));
        return ci;
    }
}