package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.Load;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoadDao {

    public LoadDao() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS loads (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "load_number TEXT NOT NULL," +
                "customer TEXT," +
                "pickup_location TEXT," +
                "drop_location TEXT," +
                "driver_id INTEGER," +
                "delivered_date DATE NOT NULL," +
                "gross_amount NUMERIC(10,2) NOT NULL," +
                "driver_percent REAL," +
                "description TEXT," +
                "paid INTEGER NOT NULL DEFAULT 0," +   // <-- Only ONE line!
                "FOREIGN KEY(driver_id) REFERENCES drivers(id)" +
                ")";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Load> getAllLoads() {
        List<Load> loads = new ArrayList<>();
        String sql = "SELECT * FROM loads";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                loads.add(toLoad(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loads;
    }

    public List<Load> getUnpaidLoads() {
        return getLoadsByPaidStatus(false);
    }

    public List<Load> getPaidLoads() {
        return getLoadsByPaidStatus(true);
    }

    private List<Load> getLoadsByPaidStatus(boolean paid) {
        List<Load> loads = new ArrayList<>();
        String sql = "SELECT * FROM loads WHERE paid = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, paid ? 1 : 0);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    loads.add(toLoad(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loads;
    }

    public void addLoad(Load load) {
        String sql = "INSERT INTO loads (load_number, customer, pickup_location, drop_location, driver_id, delivered_date, gross_amount, driver_percent, description, paid) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, load.getLoadNumber());
            pstmt.setString(2, load.getCustomer());
            pstmt.setString(3, load.getPickupLocation());
            pstmt.setString(4, load.getDropLocation());
            pstmt.setInt(5, load.getDriverId());
            pstmt.setString(6, load.getDeliveredDate() != null ? load.getDeliveredDate().toString() : null);
            pstmt.setDouble(7, load.getGrossAmount());
            pstmt.setDouble(8, load.getDriverPercent());
            pstmt.setString(9, load.getDescription());
            pstmt.setInt(10, load.isPaid() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLoad(Load load) {
        String sql = "UPDATE loads SET load_number=?, customer=?, pickup_location=?, drop_location=?, driver_id=?, delivered_date=?, gross_amount=?, driver_percent=?, description=?, paid=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, load.getLoadNumber());
            pstmt.setString(2, load.getCustomer());
            pstmt.setString(3, load.getPickupLocation());
            pstmt.setString(4, load.getDropLocation());
            pstmt.setInt(5, load.getDriverId());
            pstmt.setString(6, load.getDeliveredDate() != null ? load.getDeliveredDate().toString() : null);
            pstmt.setDouble(7, load.getGrossAmount());
            pstmt.setDouble(8, load.getDriverPercent());
            pstmt.setString(9, load.getDescription());
            pstmt.setInt(10, load.isPaid() ? 1 : 0);
            pstmt.setInt(11, load.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteLoad(int id) {
        String sql = "DELETE FROM loads WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Load getLoadById(int id) {
        String sql = "SELECT * FROM loads WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toLoad(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Load toLoad(ResultSet rs) throws SQLException {
        Load load = new Load();
        load.setId(rs.getInt("id"));
        load.setLoadNumber(rs.getString("load_number"));
        load.setCustomer(rs.getString("customer"));
        load.setPickupLocation(rs.getString("pickup_location"));
        load.setDropLocation(rs.getString("drop_location"));
        load.setDriverId(rs.getInt("driver_id"));
        String deliveredDateStr = rs.getString("delivered_date");
        load.setDeliveredDate(deliveredDateStr != null ? LocalDate.parse(deliveredDateStr) : null);
        load.setGrossAmount(rs.getDouble("gross_amount"));
        load.setDriverPercent(rs.getDouble("driver_percent"));
        load.setDescription(rs.getString("description"));
        load.setPaid(rs.getInt("paid") == 1);
        return load;
    }
}