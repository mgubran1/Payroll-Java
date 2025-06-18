package com.company.payroll.dao;

import com.company.payroll.model.FuelTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuelTransactionDao {
    private static final Logger logger = LoggerFactory.getLogger(FuelTransactionDao.class);
    private final String url = "jdbc:sqlite:fuel_transactions.db";

    public FuelTransactionDao() {
        createTableIfNotExists();
    }
	public List<FuelTransaction> getAllTransactions() {
		return getAllFuel();
	}

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS fuel_transactions (" +
                "cardNumber TEXT, tranDate TEXT, tranTime TEXT, invoice TEXT PRIMARY KEY, unit TEXT, driverName TEXT, odometer TEXT, " +
                "locationName TEXT, city TEXT, stateProv TEXT, fees TEXT, item TEXT, unitPrice TEXT, discPPU TEXT, discCost TEXT, qty TEXT, " +
                "discAmt TEXT, discType TEXT, amt TEXT, db TEXT, currency TEXT)";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Table checked/created.");
        } catch (SQLException ex) {
            logger.error("Table creation error", ex);
        }
    }

    public boolean isInvoiceImported(String invoice) {
        String sql = "SELECT 1 FROM fuel_transactions WHERE invoice = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoice);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            logger.error("isInvoiceImported error", ex);
            return false;
        }
    }

    public void addTransaction(FuelTransaction entry) {
        String sql = "INSERT OR IGNORE INTO fuel_transactions VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, entry);
            ps.executeUpdate();
            logger.info("Added transaction: {}", entry.getInvoice());
        } catch (SQLException ex) {
            logger.error("Add transaction error", ex);
        }
    }

    public void updateTransaction(FuelTransaction entry) {
        String sql = "UPDATE fuel_transactions SET cardNumber=?,tranDate=?,tranTime=?,unit=?,driverName=?,odometer=?,locationName=?,city=?," +
                "stateProv=?,fees=?,item=?,unitPrice=?,discPPU=?,discCost=?,qty=?,discAmt=?,discType=?,amt=?,db=?,currency=? WHERE invoice=?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // all except invoice (which is last param)
            ps.setString(1, entry.getCardNumber());
            ps.setString(2, entry.getTranDate());
            ps.setString(3, entry.getTranTime());
            ps.setString(4, entry.getUnit());
            ps.setString(5, entry.getDriverName());
            ps.setString(6, entry.getOdometer());
            ps.setString(7, entry.getLocationName());
            ps.setString(8, entry.getCity());
            ps.setString(9, entry.getStateProv());
            ps.setString(10, entry.getFees());
            ps.setString(11, entry.getItem());
            ps.setString(12, entry.getUnitPrice());
            ps.setString(13, entry.getDiscPPU());
            ps.setString(14, entry.getDiscCost());
            ps.setString(15, entry.getQty());
            ps.setString(16, entry.getDiscAmt());
            ps.setString(17, entry.getDiscType());
            ps.setString(18, entry.getAmt());
            ps.setString(19, entry.getDb());
            ps.setString(20, entry.getCurrency());
            ps.setString(21, entry.getInvoice());
            ps.executeUpdate();
            logger.info("Updated transaction: {}", entry.getInvoice());
        } catch (SQLException ex) {
            logger.error("Update transaction error", ex);
        }
    }

    public void deleteTransaction(String invoice) {
        String sql = "DELETE FROM fuel_transactions WHERE invoice = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoice);
            ps.executeUpdate();
            logger.info("Deleted transaction: {}", invoice);
        } catch (SQLException ex) {
            logger.error("Delete transaction error", ex);
        }
    }

    public List<FuelTransaction> getAllFuel() {
        List<FuelTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM fuel_transactions";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(readEntry(rs));
            }
        } catch (SQLException ex) {
            logger.error("Get all transactions error", ex);
        }
        return list;
    }

    // Robust alias method for compatibility with various UIs
    public List<FuelTransaction> getAllFuelTransactions() {
        return getAllFuel();
    }

    // For integration: Get by date
    public List<FuelTransaction> getTransactionsByDate(String date) {
        List<FuelTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM fuel_transactions WHERE tranDate = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readEntry(rs));
            }
        } catch (SQLException ex) {
            logger.error("Get by date error", ex);
        }
        return list;
    }

    // For integration: Get by driver
    public List<FuelTransaction> getTransactionsByDriver(String driverName) {
        List<FuelTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM fuel_transactions WHERE driverName = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, driverName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readEntry(rs));
            }
        } catch (SQLException ex) {
            logger.error("Get by driver error", ex);
        }
        return list;
    }

    public void exportToCSV(File file) throws IOException {
        List<FuelTransaction> all = getAllFuel();
        try (PrintWriter pw = new PrintWriter(file)) {
            // Write CSV header
            pw.println("Card #,Tran Date,Tran Time,Invoice,Unit,Driver Name,Odometer,Location Name,City,State/Prov,Fees,Item,Unit Price,Disc PPU,Disc Cost,Qty,Disc Amt,Disc Type,Amt,DB,Currency");
            for (FuelTransaction tx : all) {
                pw.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        csv(tx.getCardNumber()), csv(tx.getTranDate()), csv(tx.getTranTime()), csv(tx.getInvoice()),
                        csv(tx.getUnit()), csv(tx.getDriverName()), csv(tx.getOdometer()), csv(tx.getLocationName()),
                        csv(tx.getCity()), csv(tx.getStateProv()), csv(tx.getFees()), csv(tx.getItem()),
                        csv(tx.getUnitPrice()), csv(tx.getDiscPPU()), csv(tx.getDiscCost()), csv(tx.getQty()),
                        csv(tx.getDiscAmt()), csv(tx.getDiscType()), csv(tx.getAmt()), csv(tx.getDb()), csv(tx.getCurrency())
                );
            }
            logger.info("Exported {} transactions to {}", all.size(), file.getAbsolutePath());
        }
    }

    private String csv(String v) {
        if (v == null) return "";
        if (v.contains(",") || v.contains("\"")) return "\"" + v.replace("\"", "\"\"") + "\"";
        return v;
    }

    private void setParams(PreparedStatement ps, FuelTransaction entry) throws SQLException {
        ps.setString(1, entry.getCardNumber());
        ps.setString(2, entry.getTranDate());
        ps.setString(3, entry.getTranTime());
        ps.setString(4, entry.getInvoice());
        ps.setString(5, entry.getUnit());
        ps.setString(6, entry.getDriverName());
        ps.setString(7, entry.getOdometer());
        ps.setString(8, entry.getLocationName());
        ps.setString(9, entry.getCity());
        ps.setString(10, entry.getStateProv());
        ps.setString(11, entry.getFees());
        ps.setString(12, entry.getItem());
        ps.setString(13, entry.getUnitPrice());
        ps.setString(14, entry.getDiscPPU());
        ps.setString(15, entry.getDiscCost());
        ps.setString(16, entry.getQty());
        ps.setString(17, entry.getDiscAmt());
        ps.setString(18, entry.getDiscType());
        ps.setString(19, entry.getAmt());
        ps.setString(20, entry.getDb());
        ps.setString(21, entry.getCurrency());
    }

    private FuelTransaction readEntry(ResultSet rs) throws SQLException {
        FuelTransaction entry = new FuelTransaction();
        entry.setCardNumber(rs.getString(1));
        entry.setTranDate(rs.getString(2));
        entry.setTranTime(rs.getString(3));
        entry.setInvoice(rs.getString(4));
        entry.setUnit(rs.getString(5));
        entry.setDriverName(rs.getString(6));
        entry.setOdometer(rs.getString(7));
        entry.setLocationName(rs.getString(8));
        entry.setCity(rs.getString(9));
        entry.setStateProv(rs.getString(10));
        entry.setFees(rs.getString(11));
        entry.setItem(rs.getString(12));
        entry.setUnitPrice(rs.getString(13));
        entry.setDiscPPU(rs.getString(14));
        entry.setDiscCost(rs.getString(15));
        entry.setQty(rs.getString(16));
        entry.setDiscAmt(rs.getString(17));
        entry.setDiscType(rs.getString(18));
        entry.setAmt(rs.getString(19));
        entry.setDb(rs.getString(20));
        entry.setCurrency(rs.getString(21));
        return entry;
    }
}