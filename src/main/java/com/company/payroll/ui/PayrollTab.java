package com.company.payroll.ui;

import com.company.payroll.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PayrollTab: Main tab for payroll management, including sub-tabs for Loads, Fuel, Monthly Fees,
 * Cash Advances, and Other Adjustments. Includes Add/Edit/Delete logic, customizable date selection,
 * and robust driver dropdown logic.
 */
public class PayrollTab extends BorderPane {

    // Data sources
    private final ObservableList<Driver> driverList = FXCollections.observableArrayList();
    private final ObservableList<Load> loadList = FXCollections.observableArrayList();
    private final ObservableList<FuelTransaction> fuelList = FXCollections.observableArrayList();
    private final ObservableList<MonthlyFee> monthlyFeeList = FXCollections.observableArrayList();
    private final ObservableList<CashAdvance> cashAdvanceList = FXCollections.observableArrayList();
    private final ObservableList<OtherDeduction> otherDeductionList = FXCollections.observableArrayList();

    // UI Controls
    private ComboBox<Driver> cmbDriver;
    private DatePicker dpStart;
    private DatePicker dpEnd;
    private Button btnDateRangeApply;

    // Sub-tabs
    private TabPane subTabs;
    private TableView<Load> tblLoads;
    private TableView<FuelTransaction> tblFuel;
    private TableView<MonthlyFee> tblMonthlyFees;
    private TableView<CashAdvance> tblCashAdvances;
    private TableView<OtherDeduction> tblOtherAdjustments;

    // Storage for currently selected filters
    private LocalDate filterStart;
    private LocalDate filterEnd;
    private Driver filterDriver;

    public PayrollTab() {
        setPadding(new Insets(10));
        initTopSection();
        initSubTabs();
        refreshDriverList();
        // Initial filters
        filterStart = LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        filterEnd = filterStart.plusDays(6);
        updateFilterAndTables();
    }

    private void initTopSection() {
        cmbDriver = new ComboBox<>();
        cmbDriver.setPromptText("Select Driver");
        cmbDriver.setConverter(new StringConverter<>() {
            @Override
            public String toString(Driver driver) {
                return driver != null ? driver.getName() : "";
            }
            @Override
            public Driver fromString(String s) {
                return driverList.stream().filter(d -> d.getName().equals(s)).findFirst().orElse(null);
            }
        });
        cmbDriver.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterDriver = newVal;
            updateFilterAndTables();
        });

        dpStart = new DatePicker(LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1));
        dpEnd = new DatePicker(dpStart.getValue().plusDays(6));

        btnDateRangeApply = new Button("Apply Date Range");
        btnDateRangeApply.setOnAction(e -> {
            filterStart = dpStart.getValue();
            filterEnd = dpEnd.getValue();
            updateFilterAndTables();
        });

        HBox filters = new HBox(10, new Label("Driver:"), cmbDriver, new Label("Start:"), dpStart, new Label("End:"), dpEnd, btnDateRangeApply);
        filters.setPadding(new Insets(10, 0, 10, 0));
        setTop(filters);
    }

    private void initSubTabs() {
        subTabs = new TabPane();

        // Loads Tab
        tblLoads = new TableView<>(loadList);
        setupLoadTable(tblLoads);
        VBox loadBox = new VBox(10, createCRUDButtons(tblLoads, "Load"), tblLoads);
        Tab tabLoads = new Tab("Loads", loadBox);

        // Fuel Tab
        tblFuel = new TableView<>(fuelList);
        setupFuelTable(tblFuel);
        VBox fuelBox = new VBox(10, createCRUDButtons(tblFuel, "Fuel"), tblFuel);
        Tab tabFuel = new Tab("Fuel", fuelBox);

        // Monthly Fees Tab
        tblMonthlyFees = new TableView<>(monthlyFeeList);
        setupMonthlyFeeTable(tblMonthlyFees);
        VBox feeBox = new VBox(10, createCRUDButtons(tblMonthlyFees, "MonthlyFee"), tblMonthlyFees);
        Tab tabMonthlyFees = new Tab("Monthly Fees", feeBox);

        // Cash Advances Tab
        tblCashAdvances = new TableView<>(cashAdvanceList);
        setupCashAdvanceTable(tblCashAdvances);
        VBox advBox = new VBox(10, createCRUDButtons(tblCashAdvances, "CashAdvance"), tblCashAdvances);
        Tab tabCashAdvances = new Tab("Cash Advances", advBox);

        // Other Adjustments Tab
        tblOtherAdjustments = new TableView<>(otherDeductionList);
        setupOtherDeductionTable(tblOtherAdjustments);
        VBox adjBox = new VBox(10, createCRUDButtons(tblOtherAdjustments, "OtherDeduction"), tblOtherAdjustments);
        Tab tabOtherAdjustments = new Tab("Other Adjustments", adjBox);

        subTabs.getTabs().addAll(tabLoads, tabFuel, tabMonthlyFees, tabCashAdvances, tabOtherAdjustments);
        setCenter(subTabs);
    }

    private HBox createCRUDButtons(TableView<?> table, String type) {
        Button btnAdd = new Button("Add");
        Button btnEdit = new Button("Edit");
        Button btnDelete = new Button("Delete");
        btnAdd.setOnAction(e -> handleAdd(type));
        btnEdit.setOnAction(e -> handleEdit(type, table));
        btnDelete.setOnAction(e -> handleDelete(type, table));
        HBox hBox = new HBox(5, btnAdd, btnEdit, btnDelete);
        hBox.setPadding(new Insets(0, 0, 5, 0));
        return hBox;
    }

    private void handleAdd(String type) {
        switch (type) {
            case "Load" -> {
                LoadDialog dialog = new LoadDialog(getScene().getWindow(), null, driverList);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    loadList.add(dialog.getLoad());
                    updateFilterAndTables();
                }
            }
            case "Fuel" -> {
                FuelTransactionDialog dialog = new FuelTransactionDialog(getScene().getWindow(), null, driverList);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    fuelList.add(dialog.getFuelTransaction());
                    updateFilterAndTables();
                }
            }
            case "MonthlyFee" -> {
                MonthlyFeeDialog dialog = new MonthlyFeeDialog(getScene().getWindow(), null, driverList);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    monthlyFeeList.add(dialog.getMonthlyFee());
                    updateFilterAndTables();
                }
            }
            case "CashAdvance" -> {
                CashAdvanceDialog dialog = new CashAdvanceDialog(getScene().getWindow(), null, driverList);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    cashAdvanceList.add(dialog.getCashAdvance());
                    updateFilterAndTables();
                }
            }
            case "OtherDeduction" -> {
                OtherDeductionDialog dialog = new OtherDeductionDialog(getScene().getWindow(), null, driverList);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    otherDeductionList.add(dialog.getOtherDeduction());
                    updateFilterAndTables();
                }
            }
        }
    }

    private void handleEdit(String type, TableView<?> table) {
        Object selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a record to edit.");
            return;
        }
        switch (type) {
            case "Load" -> {
                LoadDialog dialog = new LoadDialog(getScene().getWindow(), (Load) selected, driverList);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    table.refresh();
                    updateFilterAndTables();
                }
            }
            case "Fuel" -> {
                FuelTransactionDialog dialog = new FuelTransactionDialog(getScene().getWindow(), (FuelTransaction) selected, driverList);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    table.refresh();
                    updateFilterAndTables();
                }
            }
            case "MonthlyFee" -> {
                MonthlyFeeDialog dialog = new MonthlyFeeDialog(getScene().getWindow(), (MonthlyFee) selected, driverList);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    table.refresh();
                    updateFilterAndTables();
                }
            }
            case "CashAdvance" -> {
                CashAdvanceDialog dialog = new CashAdvanceDialog(getScene().getWindow(), (CashAdvance) selected, driverList);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    table.refresh();
                    updateFilterAndTables();
                }
            }
            case "OtherDeduction" -> {
                OtherDeductionDialog dialog = new OtherDeductionDialog(getScene().getWindow(), (OtherDeduction) selected, driverList);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    table.refresh();
                    updateFilterAndTables();
                }
            }
        }
    }

    private void handleDelete(String type, TableView<?> table) {
        Object selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a record to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the selected record?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES) return;
        switch (type) {
            case "Load" -> loadList.remove(selected);
            case "Fuel" -> fuelList.remove(selected);
            case "MonthlyFee" -> monthlyFeeList.remove(selected);
            case "CashAdvance" -> cashAdvanceList.remove(selected);
            case "OtherDeduction" -> otherDeductionList.remove(selected);
        }
        updateFilterAndTables();
    }

    private void updateFilterAndTables() {
        // Filter and refresh all tables based on filterDriver and filterStart/filterEnd
        tblLoads.setItems(loadList.filtered(l -> filterMatch(l.getDeliveredDate(), l.getDriver())));
        tblFuel.setItems(fuelList.filtered(f -> filterMatch(f.getTranDate(), f.getDriver())));
        tblMonthlyFees.setItems(monthlyFeeList.filtered(m -> filterMatch(m.getDueDate(), m.getDriver())));
        tblCashAdvances.setItems(cashAdvanceList.filtered(c -> filterMatch(c.getAdvanceDate(), c.getDriver())));
        tblOtherAdjustments.setItems(otherDeductionList.filtered(o -> filterMatch(o.getDate(), o.getDriver())));
    }

    private boolean filterMatch(LocalDate date, Driver driver) {
        boolean inDate = (date == null || (date.compareTo(filterStart) >= 0 && date.compareTo(filterEnd) <= 0));
        boolean driverOk = (filterDriver == null || Objects.equals(driver, filterDriver));
        return inDate && driverOk;
    }

    private void refreshDriverList() {
        // TODO: Load drivers from database/service layer if needed.
        // For demo, just some dummy data:
        driverList.clear();
        driverList.addAll(
                new Driver(1, "Alice Smith", "123-456-7890", "alice@example.com", true, 5.0, 70.0, 30.0, true, ""),
                new Driver(2, "Bob Johnson", "555-123-4567", "bob@example.com", true, 7.0, 65.0, 35.0, false, "")
        );
        cmbDriver.setItems(driverList);
    }

    private void setupLoadTable(TableView<Load> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Load, String> colCustomer = new TableColumn<>("Customer");
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
        TableColumn<Load, LocalDate> colDelivered = new TableColumn<>("Delivered");
        colDelivered.setCellValueFactory(new PropertyValueFactory<>("deliveredDate"));
        TableColumn<Load, Double> colGross = new TableColumn<>("Gross");
        colGross.setCellValueFactory(new PropertyValueFactory<>("grossAmount"));
        TableColumn<Load, Driver> colDriver = new TableColumn<>("Driver");
        colDriver.setCellValueFactory(new PropertyValueFactory<>("driver"));
        TableColumn<Load, Double> colDriverPercent = new TableColumn<>("Driver %");
        colDriverPercent.setCellValueFactory(new PropertyValueFactory<>("driverPercent"));
        table.getColumns().setAll(colCustomer, colDelivered, colGross, colDriver, colDriverPercent);
    }

    private void setupFuelTable(TableView<FuelTransaction> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<FuelTransaction, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("tranDate"));
        TableColumn<FuelTransaction, String> colVendor = new TableColumn<>("Vendor");
        colVendor.setCellValueFactory(new PropertyValueFactory<>("vendor"));
        TableColumn<FuelTransaction, Double> colGallons = new TableColumn<>("Gallons");
        colGallons.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<FuelTransaction, Double> colTotalCost = new TableColumn<>("Total Cost");
        colTotalCost.setCellValueFactory(new PropertyValueFactory<>("amt"));
        TableColumn<FuelTransaction, Driver> colDriver = new TableColumn<>("Driver");
        colDriver.setCellValueFactory(new PropertyValueFactory<>("driver"));
        table.getColumns().setAll(colDate, colVendor, colGallons, colTotalCost, colDriver);
    }

    private void setupMonthlyFeeTable(TableView<MonthlyFee> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<MonthlyFee, Driver> colDriver = new TableColumn<>("Driver");
        colDriver.setCellValueFactory(new PropertyValueFactory<>("driver"));
        TableColumn<MonthlyFee, LocalDate> colDueDate = new TableColumn<>("Due Date");
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        TableColumn<MonthlyFee, String> colType = new TableColumn<>("Fee Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("feeType"));
        TableColumn<MonthlyFee, Double> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<MonthlyFee, Double> colWeeklyFee = new TableColumn<>("Weekly Fee");
        colWeeklyFee.setCellValueFactory(new PropertyValueFactory<>("weeklyFee"));
        TableColumn<MonthlyFee, String> colNotes = new TableColumn<>("Notes");
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        table.getColumns().setAll(colDriver, colDueDate, colType, colAmount, colWeeklyFee, colNotes);
    }

    private void setupCashAdvanceTable(TableView<CashAdvance> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<CashAdvance, Driver> colDriver = new TableColumn<>("Driver");
        colDriver.setCellValueFactory(new PropertyValueFactory<>("driver"));
        TableColumn<CashAdvance, LocalDate> colDate = new TableColumn<>("Advance Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("advanceDate"));
        TableColumn<CashAdvance, Double> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<CashAdvance, String> colNotes = new TableColumn<>("Notes");
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        table.getColumns().setAll(colDriver, colDate, colAmount, colNotes);
    }

    private void setupOtherDeductionTable(TableView<OtherDeduction> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<OtherDeduction, Driver> colDriver = new TableColumn<>("Driver");
        colDriver.setCellValueFactory(new PropertyValueFactory<>("driver"));
        TableColumn<OtherDeduction, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<OtherDeduction, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<OtherDeduction, Double> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<OtherDeduction, String> colNotes = new TableColumn<>("Notes");
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        table.getColumns().setAll(colDriver, colDate, colType, colAmount, colNotes);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }
}