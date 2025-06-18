package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.Payroll;

import java.sql.*;
import java.time.LocalDate;
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
        String sql = "INSERT INTO payrolls (driver_id, from_date, to_date, gross, service_fee, gross_after_service_fee, fuel, gross_after_fuel, driver_share, company_share, other_deductions, net) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, payroll.getDriverId());
            pstmt.setDate(2, Date.valueOf(payroll.getFromDate()));
            pstmt.setDate(3, Date.valueOf(payroll.getToDate()));
            pstmt.setDouble(4, payroll.getGross());
            pstmt.setDouble(5, payroll.getServiceFee());
            pstmt.setDouble(6, payroll.getGrossAfterServiceFee());
            pstmt.setDouble(7, payroll.getFuel());
            pstmt.setDouble(8, payroll.getGrossAfterFuel());
            pstmt.setDouble(9, payroll.getDriverShare());
            pstmt.setDouble(10, payroll.getCompanyShare());
            pstmt.setDouble(11, payroll.getOtherDeductions());
            pstmt.setDouble(12, payroll.getNet());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payroll.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Payroll toPayroll(ResultSet rs) throws SQLException {
        Payroll p = new Payroll();
        p.setId(rs.getInt("id"));
        p.setDriverId(rs.getInt("driver_id"));
        p.setFromDate(rs.getDate("from_date").toLocalDate());
        p.setToDate(rs.getDate("to_date").toLocalDate());
        p.setGross(rs.getDouble("gross"));
        p.setServiceFee(rs.getDouble("service_fee"));
        p.setGrossAfterServiceFee(rs.getDouble("gross_after_service_fee"));
        p.setFuel(rs.getDouble("fuel"));
        p.setGrossAfterFuel(rs.getDouble("gross_after_fuel"));
        p.setDriverShare(rs.getDouble("driver_share"));
        p.setCompanyShare(rs.getDouble("company_share"));
        p.setOtherDeductions(rs.getDouble("other_deductions"));
        p.setNet(rs.getDouble("net"));
        return p;
    }

    // Add update, delete, and getById methods as needed.
}