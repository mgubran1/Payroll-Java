package com.company.payroll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:payroll_system.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void init() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Enforce foreign key constraints
            stmt.execute("PRAGMA foreign_keys = ON;");

            // Only CREATE TABLE IF NOT EXISTS (do not drop tables in production!)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS drivers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    truck_id TEXT,
                    driver_percent REAL NOT NULL DEFAULT 0.0,
                    fuel_discount_eligible INTEGER NOT NULL DEFAULT 0,
                    company_service_fee_percent REAL NOT NULL DEFAULT 0.0,
                    company_percent REAL NOT NULL DEFAULT 0.0,
                    drive_type TEXT DEFAULT 'Owner Operator',
                    phone_number TEXT,
                    cdl_expiry DATE,
                    medical_expiry DATE,
                    license_number TEXT,
                    drivers_llc TEXT,
                    phone TEXT,
                    email TEXT,
                    active INTEGER DEFAULT 1,
                    notes TEXT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS payrolls (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    driver_id INTEGER,
                    week_start DATE NOT NULL,
                    week_end DATE NOT NULL,
                    gross NUMERIC(10,2) NOT NULL,
                    deductions NUMERIC(10,2) NOT NULL,
                    net NUMERIC(10,2) NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(driver_id) REFERENCES drivers(id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS loads (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    load_number TEXT NOT NULL,
                    customer TEXT,
                    pickup_location TEXT,
                    drop_location TEXT,
                    driver_id INTEGER,
                    delivered_date DATE NOT NULL,
                    gross_amount NUMERIC(10,2) NOT NULL,
                    driver_percent REAL,
                    description TEXT,
                    paid INTEGER NOT NULL DEFAULT 0,
                    payroll_id INTEGER,
                    FOREIGN KEY(driver_id) REFERENCES drivers(id),
                    FOREIGN KEY(payroll_id) REFERENCES payrolls(id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS fuel_transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    driver_id INTEGER,
                    truck_id TEXT,
                    tran_date DATE NOT NULL,
                    card_number TEXT,
                    invoice TEXT,
                    amount NUMERIC(10,2) NOT NULL,
                    disc_amt NUMERIC(10,2),
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    vendor TEXT,
                    notes TEXT,
                    FOREIGN KEY(driver_id) REFERENCES drivers(id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS payroll_lines (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    payroll_id INTEGER,
                    load_id INTEGER,
                    fuel_transaction_id INTEGER,
                    description TEXT,
                    amount NUMERIC(10,2) NOT NULL,
                    FOREIGN KEY(payroll_id) REFERENCES payrolls(id),
                    FOREIGN KEY(load_id) REFERENCES loads(id),
                    FOREIGN KEY(fuel_transaction_id) REFERENCES fuel_transactions(id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS other_deductions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    payroll_id INTEGER,
                    driver_id INTEGER,
                    date DATE NOT NULL,
                    amount NUMERIC(10,2) NOT NULL,
                    reason TEXT NOT NULL,
                    reimbursed INTEGER DEFAULT 0,
                    disc_amt NUMERIC(10,2),
                    FOREIGN KEY(payroll_id) REFERENCES payrolls(id),
                    FOREIGN KEY(driver_id) REFERENCES drivers(id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS company_income (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    date DATE NOT NULL,
                    customer TEXT,
                    amount NUMERIC(12,2) NOT NULL,
                    notes TEXT,
                    payroll_id INTEGER,
                    type TEXT NOT NULL DEFAULT 'Manual',
                    FOREIGN KEY(payroll_id) REFERENCES payrolls(id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS audit_log (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    action_type TEXT,
                    user TEXT,
                    driver_id INTEGER,
                    entity_type TEXT,
                    entity_id INTEGER,
                    details TEXT,
                    FOREIGN KEY(driver_id) REFERENCES drivers(id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS business_expenses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    date DATE NOT NULL,
                    category TEXT NOT NULL,
                    amount NUMERIC(10,2) NOT NULL,
                    description TEXT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS maintenance_records (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    date DATE NOT NULL,
                    truck_id TEXT,
                    trailer_number TEXT,
                    category TEXT NOT NULL,
                    amount NUMERIC(10,2) NOT NULL,
                    description TEXT NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS trailers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    trailer_number TEXT NOT NULL UNIQUE,
                    vin TEXT NOT NULL UNIQUE,
                    plate_number TEXT,
                    plate_expiry DATE,
                    insurance_expiry DATE,
                    make TEXT,
                    model TEXT,
                    year INTEGER,
                    notes TEXT,
                    active INTEGER DEFAULT 1,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS monthly_fees (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    driver_id INTEGER NOT NULL,
                    fee_type TEXT NOT NULL,
                    amount NUMERIC(10,2) NOT NULL,
                    due_date DATE NOT NULL,
                    weekly_fee NUMERIC(10,2) NOT NULL,
                    notes TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(driver_id) REFERENCES drivers(id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS cash_advance (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    driver_id INTEGER,
                    amount NUMERIC(10,2) NOT NULL,
                    issued_date DATE NOT NULL,
                    weekly_repayment NUMERIC(10,2) NOT NULL,
                    notes TEXT,
                    status TEXT DEFAULT 'pending',
                    assigned_payroll_id INTEGER,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(driver_id) REFERENCES drivers(id),
                    FOREIGN KEY(assigned_payroll_id) REFERENCES payrolls(id)
                );
            """);

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}