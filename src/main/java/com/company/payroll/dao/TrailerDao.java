package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.Trailer;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrailerDao {
    public List<Trailer> getAllTrailers() {
        List<Trailer> trailers = new ArrayList<>();
        String sql = "SELECT * FROM trailers";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                trailers.add(toTrailer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trailers;
    }

    public void addTrailer(Trailer t) {
        String sql = "INSERT INTO trailers (trailer_number, vin, plate_number, plate_expiry, insurance_expiry, make, model, year, notes, active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, t.getTrailerNumber());
            pstmt.setString(2, t.getVin());
            pstmt.setString(3, t.getPlateNumber());
            if (t.getPlateExpiry() == null) {
                pstmt.setNull(4, Types.DATE);
            } else {
                pstmt.setDate(4, Date.valueOf(t.getPlateExpiry()));
            }
            if (t.getInsuranceExpiry() == null) {
                pstmt.setNull(5, Types.DATE);
            } else {
                pstmt.setDate(5, Date.valueOf(t.getInsuranceExpiry()));
            }
            pstmt.setString(6, t.getMake());
            pstmt.setString(7, t.getModel());
            if (t.getYear() == null) {
                pstmt.setNull(8, Types.INTEGER);
            } else {
                pstmt.setInt(8, t.getYear());
            }
            pstmt.setString(9, t.getNotes());
            pstmt.setBoolean(10, t.isActive());
            pstmt.setTimestamp(11, t.getCreatedAt() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(t.getCreatedAt()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTrailer(Trailer t) {
        String sql = "UPDATE trailers SET trailer_number=?, vin=?, plate_number=?, plate_expiry=?, insurance_expiry=?, make=?, model=?, year=?, notes=?, active=?, created_at=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, t.getTrailerNumber());
            pstmt.setString(2, t.getVin());
            pstmt.setString(3, t.getPlateNumber());
            if (t.getPlateExpiry() == null) {
                pstmt.setNull(4, Types.DATE);
            } else {
                pstmt.setDate(4, Date.valueOf(t.getPlateExpiry()));
            }
            if (t.getInsuranceExpiry() == null) {
                pstmt.setNull(5, Types.DATE);
            } else {
                pstmt.setDate(5, Date.valueOf(t.getInsuranceExpiry()));
            }
            pstmt.setString(6, t.getMake());
            pstmt.setString(7, t.getModel());
            if (t.getYear() == null) {
                pstmt.setNull(8, Types.INTEGER);
            } else {
                pstmt.setInt(8, t.getYear());
            }
            pstmt.setString(9, t.getNotes());
            pstmt.setBoolean(10, t.isActive());
            pstmt.setTimestamp(11, t.getCreatedAt() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(t.getCreatedAt()));
            pstmt.setInt(12, t.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTrailer(int id) {
        String sql = "DELETE FROM trailers WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Trailer toTrailer(ResultSet rs) throws SQLException {
        Trailer t = new Trailer();
        t.setId(rs.getInt("id"));
        t.setTrailerNumber(rs.getString("trailer_number"));
        t.setVin(rs.getString("vin"));
        t.setPlateNumber(rs.getString("plate_number"));
        Date d = rs.getDate("plate_expiry");
        t.setPlateExpiry(d == null ? null : d.toLocalDate());
        d = rs.getDate("insurance_expiry");
        t.setInsuranceExpiry(d == null ? null : d.toLocalDate());
        t.setMake(rs.getString("make"));
        t.setModel(rs.getString("model"));
        int year = rs.getInt("year");
        t.setYear(rs.wasNull() ? null : year);
        t.setNotes(rs.getString("notes"));
        t.setActive(rs.getBoolean("active"));
        Timestamp ts = rs.getTimestamp("created_at");
        t.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
        return t;
    }
}