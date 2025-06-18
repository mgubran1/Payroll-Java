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
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

public class PayrollTab extends BorderPane {

    private final ObservableList<Driver> driverList = FXCollections.observableArrayList();
    private final ObservableList<Load> loadList = FXCollections.observableArrayList();
    private final ObservableList<FuelTransaction> fuelList = FXCollections.observableArrayList();
    private final ObservableList<MonthlyFee> monthlyFeeList = FXCollections.observableArrayList();
    private final ObservableList<CashAdvance> cashAdvanceList = FXCollections.observableArrayList();
    private final ObservableList<OtherDeduction> otherDeductionList = FXCollections.observableArrayList();

    private ComboBox<Driver> cmbDriver;
    private DatePicker dpStart;
    private DatePicker dpEnd;
    private Button btnDateRangeApply;

    private TabPane subTabs;
    private TableView<Load> tblLoads;
    private TableView<FuelTransaction> tblFuel;
    private TableView<MonthlyFee> tblMonthlyFees;
    private TableView<CashAdvance> tblCashAdvances;
    private TableView<OtherDeduction> tblOtherAdjustments;

    private LocalDate filterStart;
    private LocalDate filterEnd;
    private Driver filterDriver;

    private Map<Integer, Driver> driverIdMap = new HashMap<>();

    public PayrollTab() {
        setPadding(new Insets(10));
        initTopSection();
        initSubTabs();
        refreshDriverList();
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

        tblLoads = new TableView<>(loadList);
        setupLoadTable(tblLoads);
        VBox loadBox = new VBox(10, createCRUDButtons(tblLoads, "Load"), tblLoads);
        Tab tabLoads = new Tab("Loads", loadBox);

        tblFuel = new TableView<>(fuelList);
        setupFuelTable(tblFuel);
        VBox fuelBox = new VBox(10, createCRUDButtons(tblFuel, "Fuel"), tblFuel);
        Tab tabFuel = new Tab("Fuel", fuelBox);

        tblMonthlyFees = new TableView<>(monthlyFeeList);
        setupMonthlyFeeTable(tblMonthlyFees);
        VBox feeBox = new VBox(10, createCRUDButtons(tblMonthlyFees, "MonthlyFee"), tblMonthlyFees);
        Tab tabMonthlyFees = new Tab("Monthly Fees", feeBox);

        tblCashAdvances = new TableView<>(cashAdvanceList);
        setupCashAdvanceTable(tblCashAdvances);
        VBox advBox = new VBox(10, createCRUDButtons(tblCashAdvances, "CashAdvance"), tblCashAdvances);
        Tab tabCashAdvances = new Tab("Cash Advances", advBox);

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
                LoadDialog dialog = new LoadDialog((Stage) getScene().getWindow(), null);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    loadList.add(dialog.getLoad());
                    updateFilterAndTables();
                }
            }
            case "Fuel" -> {
                FuelTransactionDialog dialog = new FuelTransactionDialog((Stage) getScene().getWindow(), null);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    fuelList.add(dialog.getFuelTransaction());
                    updateFilterAndTables();
                }
            }
            case "MonthlyFee" -> {
                MonthlyFeeDialog dialog = new MonthlyFeeDialog((Stage) getScene().getWindow(), null);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    monthlyFeeList.add(dialog.getMonthlyFee());
                    updateFilterAndTables();
                }
            }
            case "CashAdvance" -> {
                CashAdvanceDialog dialog = new CashAdvanceDialog((Stage) getScene().getWindow(), null);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    cashAdvanceList.add(dialog.getCashAdvance());
                    updateFilterAndTables();
                }
            }
            case "OtherDeduction" -> {
                OtherDeductionDialog dialog = new OtherDeductionDialog((Stage) getScene().getWindow(), null);
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
                LoadDialog dialog = new LoadDialog((Stage) getScene().getWindow(), (Load) selected);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    table.refresh();
                    updateFilterAndTables();
                }
            }
            case "Fuel" -> {
                FuelTransactionDialog dialog = new FuelTransactionDialog((Stage) getScene().getWindow(), (FuelTransaction) selected);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    table.refresh();
                    updateFilterAndTables();
                }
            }
            case "MonthlyFee" -> {
                MonthlyFeeDialog dialog = new MonthlyFeeDialog((Stage) getScene().getWindow(), (MonthlyFee) selected);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    table.refresh();
                    updateFilterAndTables();
                }
            }
            case "CashAdvance" -> {
                CashAdvanceDialog dialog = new CashAdvanceDialog((Stage) getScene().getWindow(), (CashAdvance) selected);
                dialog.showAndWait();
                if (dialog.isSaved()) {
                    table.refresh();
                    updateFilterAndTables();
                }
            }
            case "OtherDeduction" -> {
                OtherDeductionDialog dialog = new OtherDeductionDialog((Stage) getScene().getWindow(), (OtherDeduction) selected);
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
        tblLoads.setItems(loadList.filtered(l -> filterMatch(l.getDeliveredDate(), getDriverById(l.getDriverId()))));
        // For FuelTransaction, model uses String for tranDate, but UI expects LocalDate. Convert if possible.
        tblFuel.setItems(fuelList.filtered(f -> {
            LocalDate date = null;
            try { date = f.getTranDate() == null ? null : LocalDate.parse(f.getTranDate()); } catch (Exception ignored) {}
            return filterMatch(date, getDriverById(
                    f.getDriverId() != null ? f.getDriverId() : 0));
        }));
        tblMonthlyFees.setItems(monthlyFeeList.filtered(m -> filterMatch(m.getDueDate(), getDriverById(m.getDriverId()))));
        tblCashAdvances.setItems(cashAdvanceList.filtered(c -> filterMatch(c.getAdvanceDate(), getDriverById(c.getDriverId()))));
        tblOtherAdjustments.setItems(otherDeductionList.filtered(o -> filterMatch(o.getDate(), getDriverById(o.getDriverId()))));
    }

    private boolean filterMatch(LocalDate date, Driver driver) {
        boolean inDate = (date == null || (date.compareTo(filterStart) >= 0 && date.compareTo(filterEnd) <= 0));
        boolean driverOk = (filterDriver == null || (driver != null && driver.equals(filterDriver)));
        return inDate && driverOk;
    }

    private void refreshDriverList() {
        driverList.clear();
        // Use default constructor and setters for Driver (so you don't hit the 'no suitable constructor' error)
        Driver d1 = new Driver();
        d1.setId(1); d1.setName("Alice Smith"); d1.setPhone("123-456-7890"); d1.setEmail("alice@example.com");
        d1.setActive(true); d1.setCompanyServiceFeePercent(5.0); d1.setDriverPercent(70.0);
        d1.setCompanyPercent(30.0); d1.setFuelDiscountEligible(true); d1.setNotes("");
        Driver d2 = new Driver();
        d2.setId(2); d2.setName("Bob Johnson"); d2.setPhone("555-123-4567"); d2.setEmail("bob@example.com");
        d2.setActive(true); d2.setCompanyServiceFeePercent(7.0); d2.setDriverPercent(65.0);
        d2.setCompanyPercent(35.0); d2.setFuelDiscountEligible(false); d2.setNotes("");
        driverList.addAll(d1, d2);
        driverIdMap = driverList.stream().collect(Collectors.toMap(Driver::getId, d -> d));
        cmbDriver.setItems(driverList);
    }

    private Driver getDriverById(int driverId) {
        return driverIdMap.get(driverId);
    }

    private void setupLoadTable(TableView<Load> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Load, String> colCustomer = new TableColumn<>("Customer");
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
        TableColumn<Load, LocalDate> colDelivered = new TableColumn<>("Delivered");
        colDelivered.setCellValueFactory(new PropertyValueFactory<>("deliveredDate"));
        TableColumn<Load, Double> colGross = new TableColumn<>("Gross");
        colGross.setCellValueFactory(new PropertyValueFactory<>("grossAmount"));
        TableColumn<Load, Integer> colDriverId = new TableColumn<>("Driver");
        colDriverId.setCellValueFactory(new PropertyValueFactory<>("driverId"));
        colDriverId.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer driverId, boolean empty) {
                super.updateItem(driverId, empty);
                if (empty || driverId == null) {
                    setText("");
                } else {
                    Driver driver = getDriverById(driverId);
                    setText(driver != null ? driver.getName() : String.valueOf(driverId));
                }
            }
        });
        TableColumn<Load, Double> colDriverPercent = new TableColumn<>("Driver %");
        colDriverPercent.setCellValueFactory(new PropertyValueFactory<>("driverPercent"));
        table.getColumns().setAll(colCustomer, colDelivered, colGross, colDriverId, colDriverPercent);
    }

    private void setupFuelTable(TableView<FuelTransaction> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<FuelTransaction, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("tranDate"));
        TableColumn<FuelTransaction, String> colVendor = new TableColumn<>("Vendor");
        colVendor.setCellValueFactory(new PropertyValueFactory<>("vendor"));
        TableColumn<FuelTransaction, String> colGallons = new TableColumn<>("Gallons");
        colGallons.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<FuelTransaction, String> colTotalCost = new TableColumn<>("Total Cost");
        colTotalCost.setCellValueFactory(new PropertyValueFactory<>("amt"));
        TableColumn<FuelTransaction, Integer> colDriverId = new TableColumn<>("Driver");
        colDriverId.setCellValueFactory(new PropertyValueFactory<>("driverId"));
        colDriverId.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer driverId, boolean empty) {
                super.updateItem(driverId, empty);
                if (empty || driverId == null) {
                    setText("");
                } else {
                    Driver driver = getDriverById(driverId);
                    setText(driver != null ? driver.getName() : String.valueOf(driverId));
                }
            }
        });
        table.getColumns().setAll(colDate, colVendor, colGallons, colTotalCost, colDriverId);
    }

    private void setupMonthlyFeeTable(TableView<MonthlyFee> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<MonthlyFee, Integer> colDriverId = new TableColumn<>("Driver");
        colDriverId.setCellValueFactory(new PropertyValueFactory<>("driverId"));
        colDriverId.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer driverId, boolean empty) {
                super.updateItem(driverId, empty);
                if (empty || driverId == null) {
                    setText("");
                } else {
                    Driver driver = getDriverById(driverId);
                    setText(driver != null ? driver.getName() : String.valueOf(driverId));
                }
            }
        });
        TableColumn<MonthlyFee, LocalDate> colDueDate = new TableColumn<>("Due Date");
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        TableColumn<MonthlyFee, String> colType = new TableColumn<>("Fee Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("feeType"));
        TableColumn<MonthlyFee, BigDecimal> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<MonthlyFee, BigDecimal> colWeeklyFee = new TableColumn<>("Weekly Fee");
        colWeeklyFee.setCellValueFactory(new PropertyValueFactory<>("weeklyFee"));
        TableColumn<MonthlyFee, String> colNotes = new TableColumn<>("Notes");
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        table.getColumns().setAll(colDriverId, colDueDate, colType, colAmount, colWeeklyFee, colNotes);
    }

    private void setupCashAdvanceTable(TableView<CashAdvance> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<CashAdvance, Integer> colDriverId = new TableColumn<>("Driver");
        colDriverId.setCellValueFactory(new PropertyValueFactory<>("driverId"));
        colDriverId.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer driverId, boolean empty) {
                super.updateItem(driverId, empty);
                if (empty || driverId == null) {
                    setText("");
                } else {
                    Driver driver = getDriverById(driverId);
                    setText(driver != null ? driver.getName() : String.valueOf(driverId));
                }
            }
        });
        TableColumn<CashAdvance, LocalDate> colDate = new TableColumn<>("Advance Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("advanceDate"));
        TableColumn<CashAdvance, BigDecimal> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<CashAdvance, String> colNotes = new TableColumn<>("Notes");
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        table.getColumns().setAll(colDriverId, colDate, colAmount, colNotes);
    }

    private void setupOtherDeductionTable(TableView<OtherDeduction> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<OtherDeduction, Integer> colDriverId = new TableColumn<>("Driver");
        colDriverId.setCellValueFactory(new PropertyValueFactory<>("driverId"));
        colDriverId.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer driverId, boolean empty) {
                super.updateItem(driverId, empty);
                if (empty || driverId == null) {
                    setText("");
                } else {
                    Driver driver = getDriverById(driverId);
                    setText(driver != null ? driver.getName() : String.valueOf(driverId));
                }
            }
        });
        TableColumn<OtherDeduction, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<OtherDeduction, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<OtherDeduction, Double> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<OtherDeduction, String> colNotes = new TableColumn<>("Notes");
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        table.getColumns().setAll(colDriverId, colDate, colType, colAmount, colNotes);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }
}