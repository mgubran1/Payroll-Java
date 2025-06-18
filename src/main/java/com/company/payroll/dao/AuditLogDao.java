package com.company.payroll.dao;

import com.company.payroll.Database;
import com.company.payroll.model.AuditLog;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDao {
    public List<AuditLog> getAllAuditLogs() {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM audit_log";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                logs.add(toAuditLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public void addAuditLog(AuditLog al) {
        String sql = "INSERT INTO audit_log (timestamp, action_type, user, driver_id, entity_type, entity_id, details) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, al.getTimestamp() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(al.getTimestamp()));
            pstmt.setString(2, al.getActionType());
            pstmt.setString(3, al.getUser());
            if (al.getDriverId() == null) {
                pstmt.setNull(4, Types.INTEGER);
            } else {
                pstmt.setInt(4, al.getDriverId());
            }
            pstmt.setString(5, al.getEntityType());
            if (al.getEntityId() == null) {
                pstmt.setNull(6, Types.INTEGER);
            } else {
                pstmt.setInt(6, al.getEntityId());
            }
            pstmt.setString(7, al.getDetails());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAuditLog(AuditLog al) {
        String sql = "UPDATE audit_log SET timestamp=?, action_type=?, user=?, driver_id=?, entity_type=?, entity_id=?, details=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, al.getTimestamp() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(al.getTimestamp()));
            pstmt.setString(2, al.getActionType());
            pstmt.setString(3, al.getUser());
            if (al.getDriverId() == null) {
                pstmt.setNull(4, Types.INTEGER);
            } else {
                pstmt.setInt(4, al.getDriverId());
            }
            pstmt.setString(5, al.getEntityType());
            if (al.getEntityId() == null) {
                pstmt.setNull(6, Types.INTEGER);
            } else {
                pstmt.setInt(6, al.getEntityId());
            }
            pstmt.setString(7, al.getDetails());
            pstmt.setInt(8, al.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAuditLog(int id) {
        String sql = "DELETE FROM audit_log WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private AuditLog toAuditLog(ResultSet rs) throws SQLException {
        AuditLog al = new AuditLog();
        al.setId(rs.getInt("id"));
        Timestamp ts = rs.getTimestamp("timestamp");
        al.setTimestamp(ts == null ? null : ts.toLocalDateTime());
        al.setActionType(rs.getString("action_type"));
        al.setUser(rs.getString("user"));
        int driverId = rs.getInt("driver_id");
        al.setDriverId(rs.wasNull() ? null : driverId);
        al.setEntityType(rs.getString("entity_type"));
        int entityId = rs.getInt("entity_id");
        al.setEntityId(rs.wasNull() ? null : entityId);
        al.setDetails(rs.getString("details"));
        return al;
    }
}