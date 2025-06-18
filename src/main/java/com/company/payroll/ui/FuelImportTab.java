package com.company.payroll.ui;

import com.company.payroll.model.FuelTransaction;
import com.company.payroll.dao.FuelTransactionDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opencsv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FuelImportTab extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(FuelImportTab.class);

    private TableView<FuelTransaction> table;
    private ObservableList<FuelTransaction> data;
    private FuelTransactionDao fuelTransactionDao = new FuelTransactionDao();
    private TextField searchField;

    public FuelImportTab() {
        data = FXCollections.observableArrayList(fuelTransactionDao.getAllTransactions());
        table = new TableView<>(data);

        String[] columns = {
            "Card #", "Tran Date", "Tran Time", "Invoice", "Unit", "Driver Name", "Odometer",
            "Location Name", "City", "State/ Prov", "Fees", "Item", "Unit Price", "Disc PPU",
            "Disc Cost", "Qty", "Disc Amt", "Disc Type", "Amt", "DB", "Currency"
        };

        String[] properties = {
            "cardNumber", "tranDate", "tranTime", "invoice", "unit", "driverName", "odometer",
            "locationName", "city", "stateProv", "fees", "item", "unitPrice", "discPPU",
            "discCost", "qty", "discAmt", "discType", "amt", "db", "currency"
        };

        for (int i = 0; i < columns.length; i++) {
            TableColumn<FuelTransaction, String> col = new TableColumn<>(columns[i]);
            col.setCellValueFactory(new PropertyValueFactory<>(properties[i]));
            col.setSortable(true);
            table.getColumns().add(col);
        }

        Button importBtn = new Button("Import");
        importBtn.setOnAction(e -> importFuelLogs());

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> onAdd());

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> onEdit());

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> onDelete());

        Button exportBtn = new Button("Export CSV");
        exportBtn.setOnAction(e -> exportToCSV());

        searchField = new TextField();
        searchField.setPromptText("Search by invoice, driver, date, etc...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));

        HBox topBar = new HBox(10, searchField, exportBtn);
        topBar.setPadding(new Insets(10, 10, 10, 10));

        HBox controls = new HBox(10, importBtn, addBtn, editBtn, deleteBtn);
        controls.setPadding(new Insets(10));

        setTop(topBar);
        setCenter(table);
        setBottom(controls);
    }

    private void applyFilter(String filter) {
        Predicate<FuelTransaction> predicate = tx -> {
            if (filter == null || filter.trim().isEmpty()) return true;
            String lower = filter.toLowerCase();
            return (tx.getInvoice() != null && tx.getInvoice().toLowerCase().contains(lower)) ||
                   (tx.getDriverName() != null && tx.getDriverName().toLowerCase().contains(lower)) ||
                   (tx.getTranDate() != null && tx.getTranDate().toLowerCase().contains(lower)) ||
                   (tx.getCardNumber() != null && tx.getCardNumber().toLowerCase().contains(lower)) ||
                   (tx.getUnit() != null && tx.getUnit().toLowerCase().contains(lower)) ||
                   (tx.getLocationName() != null && tx.getLocationName().toLowerCase().contains(lower));
        };
        data.setAll(fuelTransactionDao.getAllTransactions().stream().filter(predicate).collect(Collectors.toList()));
    }

    private void importFuelLogs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Fuel Log");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file == null) return;

        try {
            if (file.getName().toLowerCase().endsWith(".csv")) {
                importFromCSV(file);
            } else if (file.getName().toLowerCase().endsWith(".xlsx")) {
                importFromExcel(file);
            }
            logger.info("Imported file: {}", file.getName());
            refresh();
            showInfo("Import Complete", "Imported file: " + file.getName());
        } catch (Exception ex) {
            logger.error("Failed to import file", ex);
            showAlert("Import Error", "Failed to import file:\n" + ex.getMessage());
        }
    }

    private void importFromCSV(File file) throws Exception {
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] header = reader.readNext();
            String[] row;
            int importedCount = 0;
            while ((row = reader.readNext()) != null) {
                FuelTransaction entry = mapRowToEntry(row);
                if (entry != null && entry.getInvoice() != null && validate(entry) && !fuelTransactionDao.isInvoiceImported(entry.getInvoice())) {
                    fuelTransactionDao.addTransaction(entry);
                    importedCount++;
                }
            }
            logger.info("Imported {} transactions from CSV", importedCount);
        }
    }

    private void importFromExcel(File file) throws Exception {
        try (InputStream inp = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(inp)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> it = sheet.iterator();
            if (it.hasNext()) it.next(); // skip header
            int importedCount = 0;
            while (it.hasNext()) {
                Row row = it.next();
                FuelTransaction entry = mapRowToEntry(row);
                if (entry != null && entry.getInvoice() != null && validate(entry) && !fuelTransactionDao.isInvoiceImported(entry.getInvoice())) {
                    fuelTransactionDao.addTransaction(entry);
                    importedCount++;
                }
            }
            logger.info("Imported {} transactions from Excel", importedCount);
        }
    }

    // Map CSV row to FuelTransaction (21 columns)
    private FuelTransaction mapRowToEntry(String[] row) {
        if (row.length < 21) return null;
        FuelTransaction entry = new FuelTransaction();
        entry.setCardNumber(row[0]);
        entry.setTranDate(row[1]);
        entry.setTranTime(row[2]);
        entry.setInvoice(row[3]);
        entry.setUnit(row[4]);
        entry.setDriverName(row[5]);
        entry.setOdometer(row[6]);
        entry.setLocationName(row[7]);
        entry.setCity(row[8]);
        entry.setStateProv(row[9]);
        entry.setFees(row[10]);
        entry.setItem(row[11]);
        entry.setUnitPrice(row[12]);
        entry.setDiscPPU(row[13]);
        entry.setDiscCost(row[14]);
        entry.setQty(row[15]);
        entry.setDiscAmt(row[16]);
        entry.setDiscType(row[17]);
        entry.setAmt(row[18]);
        entry.setDb(row[19]);
        entry.setCurrency(row[20]);
        return entry;
    }

    // Map Excel row to FuelTransaction
    private FuelTransaction mapRowToEntry(Row row) {
        FuelTransaction entry = new FuelTransaction();
        entry.setCardNumber(getCellString(row, 0));
        entry.setTranDate(getCellString(row, 1));
        entry.setTranTime(getCellString(row, 2));
        entry.setInvoice(getCellString(row, 3));
        entry.setUnit(getCellString(row, 4));
        entry.setDriverName(getCellString(row, 5));
        entry.setOdometer(getCellString(row, 6));
        entry.setLocationName(getCellString(row, 7));
        entry.setCity(getCellString(row, 8));
        entry.setStateProv(getCellString(row, 9));
        entry.setFees(getCellString(row, 10));
        entry.setItem(getCellString(row, 11));
        entry.setUnitPrice(getCellString(row, 12));
        entry.setDiscPPU(getCellString(row, 13));
        entry.setDiscCost(getCellString(row, 14));
        entry.setQty(getCellString(row, 15));
        entry.setDiscAmt(getCellString(row, 16));
        entry.setDiscType(getCellString(row, 17));
        entry.setAmt(getCellString(row, 18));
        entry.setDb(getCellString(row, 19));
        entry.setCurrency(getCellString(row, 20));
        return entry;
    }

    private String getCellString(Row row, int idx) {
        org.apache.poi.ss.usermodel.Cell cell = row.getCell(idx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }

    private boolean validate(FuelTransaction entry) {
        if (entry.getInvoice() == null || entry.getInvoice().trim().isEmpty()) {
            showAlert("Validation Error", "Invoice is required.");
            return false;
        }
        if (entry.getTranDate() == null || entry.getTranDate().trim().isEmpty()) {
            showAlert("Validation Error", "Transaction Date is required.");
            return false;
        }
        // Add further validation as needed (date format, numeric fields etc)
        return true;
    }

    private void refresh() {
        data.setAll(fuelTransactionDao.getAllTransactions());
        applyFilter(searchField.getText()); // re-apply filter
    }

    private void showAlert(String title, String message) {
        logger.warn("{}: {}", title, message);
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        logger.info("{}: {}", title, message);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // Add/Edit/Delete/Export logic

    private void onAdd() {
        FuelTransactionEditDialog dialog = new FuelTransactionEditDialog(null);
        dialog.showAndWait().ifPresent(entry -> {
            if (!validate(entry)) return;
            if (!fuelTransactionDao.isInvoiceImported(entry.getInvoice())) {
                try {
                    fuelTransactionDao.addTransaction(entry);
                    refresh();
                    showInfo("Success", "Transaction added!");
                } catch (Exception ex) {
                    logger.error("Add failed", ex);
                    showAlert("Error", "Could not add transaction: " + ex.getMessage());
                }
            } else {
                showAlert("Duplicate Invoice", "A record with this invoice already exists.");
            }
        });
    }

    private void onEdit() {
        FuelTransaction selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a row to edit.");
            return;
        }
        FuelTransactionEditDialog dialog = new FuelTransactionEditDialog(selected);
        dialog.showAndWait().ifPresent(entry -> {
            if (!validate(entry)) return;
            try {
                fuelTransactionDao.updateTransaction(entry);
                refresh();
                showInfo("Success", "Transaction updated!");
            } catch (Exception ex) {
                logger.error("Edit failed", ex);
                showAlert("Error", "Could not update transaction: " + ex.getMessage());
            }
        });
    }

    private void onDelete() {
        FuelTransaction selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a row to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected transaction?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.setTitle("Confirm Delete");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    fuelTransactionDao.deleteTransaction(selected.getInvoice());
                    refresh();
                    showInfo("Deleted", "Transaction deleted.");
                } catch (Exception ex) {
                    logger.error("Delete failed", ex);
                    showAlert("Error", "Could not delete transaction: " + ex.getMessage());
                }
            }
        });
    }

    private void exportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Fuel Transactions to CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("fuel_transactions_" + LocalDate.now() + ".csv");
        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file == null) return;
        try {
            fuelTransactionDao.exportToCSV(file);
            showInfo("Export Complete", "Transactions exported to: " + file.getAbsolutePath());
            logger.info("Exported transactions to {}", file.getAbsolutePath());
        } catch (IOException ex) {
            logger.error("Export failed", ex);
            showAlert("Export Error", "Could not export to CSV: " + ex.getMessage());
        }
    }
}

// Helper dialog for Add/Edit
class FuelTransactionEditDialog extends Dialog<FuelTransaction> {
    private TextField[] fields = new TextField[21];
    private static final String[] LABELS = {
        "Card #", "Tran Date", "Tran Time", "Invoice", "Unit", "Driver Name", "Odometer",
        "Location Name", "City", "State/ Prov", "Fees", "Item", "Unit Price", "Disc PPU",
        "Disc Cost", "Qty", "Disc Amt", "Disc Type", "Amt", "DB", "Currency"
    };

    public FuelTransactionEditDialog(FuelTransaction entry) {
        setTitle(entry == null ? "Add Fuel Transaction" : "Edit Fuel Transaction");
        setHeaderText(null);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10));

        for (int i = 0; i < LABELS.length; i++) {
            fields[i] = new TextField();
            grid.add(new Label(LABELS[i] + ":"), 0, i);
            grid.add(fields[i], 1, i);
        }

        if (entry != null) {
            fields[0].setText(entry.getCardNumber());
            fields[1].setText(entry.getTranDate());
            fields[2].setText(entry.getTranTime());
            fields[3].setText(entry.getInvoice());
            fields[4].setText(entry.getUnit());
            fields[5].setText(entry.getDriverName());
            fields[6].setText(entry.getOdometer());
            fields[7].setText(entry.getLocationName());
            fields[8].setText(entry.getCity());
            fields[9].setText(entry.getStateProv());
            fields[10].setText(entry.getFees());
            fields[11].setText(entry.getItem());
            fields[12].setText(entry.getUnitPrice());
            fields[13].setText(entry.getDiscPPU());
            fields[14].setText(entry.getDiscCost());
            fields[15].setText(entry.getQty());
            fields[16].setText(entry.getDiscAmt());
            fields[17].setText(entry.getDiscType());
            fields[18].setText(entry.getAmt());
            fields[19].setText(entry.getDb());
            fields[20].setText(entry.getCurrency());
            // Only allow editing invoice if adding, not editing
            fields[3].setDisable(true);
        }

        getDialogPane().setContent(grid);

        setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                FuelTransaction result = new FuelTransaction();
                result.setCardNumber(fields[0].getText());
                result.setTranDate(fields[1].getText());
                result.setTranTime(fields[2].getText());
                result.setInvoice(fields[3].getText());
                result.setUnit(fields[4].getText());
                result.setDriverName(fields[5].getText());
                result.setOdometer(fields[6].getText());
                result.setLocationName(fields[7].getText());
                result.setCity(fields[8].getText());
                result.setStateProv(fields[9].getText());
                result.setFees(fields[10].getText());
                result.setItem(fields[11].getText());
                result.setUnitPrice(fields[12].getText());
                result.setDiscPPU(fields[13].getText());
                result.setDiscCost(fields[14].getText());
                result.setQty(fields[15].getText());
                result.setDiscAmt(fields[16].getText());
                result.setDiscType(fields[17].getText());
                result.setAmt(fields[18].getText());
                result.setDb(fields[19].getText());
                result.setCurrency(fields[20].getText());
                return result;
            }
            return null;
        });
    }
}