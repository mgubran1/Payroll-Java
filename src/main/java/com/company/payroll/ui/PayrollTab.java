package com.company.payroll.ui;

import com.company.payroll.dao.*;
import com.company.payroll.model.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PayrollTab uses the shared DriverRepository for its driver list,
 * so changes in the Employees tab are instantly reflected here.
 */
public class PayrollTab extends BorderPane implements DataRefreshListener {

    private final DriverDao driverDao = new DriverDao();
    private final LoadDao loadDao = new LoadDao();
    private final FuelTransactionDao fuelTransactionDao = new FuelTransactionDao();
    private final MonthlyFeeDao monthlyFeeDao = new MonthlyFeeDao();
    private final CashAdvanceDao cashAdvanceDao = new CashAdvanceDao();
    private final OtherDeductionDao otherDeductionDao = new OtherDeductionDao();

    // Get the shared driver list from DriverRepository
    private final ObservableList<Driver> driverList = DriverRepository.getInstance().getDriverList();
    private final ObservableList<Load> filteredLoads = javafx.collections.FXCollections.observableArrayList();
    private final ObservableList<FuelTransaction> filteredFuels = javafx.collections.FXCollections.observableArrayList();
    private final ObservableList<MonthlyFee> filteredFees = javafx.collections.FXCollections.observableArrayList();
    private final ObservableList<CashAdvance> filteredAdvances = javafx.collections.FXCollections.observableArrayList();
    private final ObservableList<OtherDeduction> filteredAdjustments = javafx.collections.FXCollections.observableArrayList();

    private ComboBox<Driver> driverCombo;
    private DatePicker fromDatePicker, toDatePicker;
    private Button applyRangeBtn;
    private Label grossLabel, serviceFeeLabel, grossAfterServiceFeeLabel, fuelLabel, feesLabel,
            grossAfterFuelLabel, driverShareLabel, companyShareLabel, deductionsLabel, netLabel;

    private TableView<Load> loadsTable;
    private TableView<FuelTransaction> fuelTable;
    private TableView<MonthlyFee> feesTable;
    private TableView<CashAdvance> advancesTable;
    private TableView<OtherDeduction> adjustmentsTable;

    public PayrollTab() {
        buildUI();
        // No need to manually refresh: driverList is shared and auto-updates from repository
        DriverRepository.getInstance().refreshDriversFromDatabase();
        selectFirstDriverIfNeeded();
        refreshPayroll();
        // Listen for changes to the driver list to select first driver if needed
        driverList.addListener((javafx.collections.ListChangeListener<Driver>) change -> selectFirstDriverIfNeeded());
    }

    private void buildUI() {
        driverCombo = new ComboBox<>(driverList);
        driverCombo.setPromptText("Select Driver");
        driverCombo.setMinWidth(220);
        driverCombo.setConverter(new StringConverter<Driver>() {
            @Override public String toString(Driver d) { return d != null ? d.getName() : ""; }
            @Override public Driver fromString(String s) { return null; }
        });
        driverCombo.valueProperty().addListener((obs, old, val) -> refreshPayroll());

        fromDatePicker = new DatePicker(LocalDate.now().with(java.time.DayOfWeek.MONDAY));
        toDatePicker = new DatePicker(LocalDate.now().with(java.time.DayOfWeek.SUNDAY));
        fromDatePicker.valueProperty().addListener((obs, old, val) -> refreshPayroll());
        toDatePicker.valueProperty().addListener((obs, old, val) -> refreshPayroll());

        applyRangeBtn = new Button("Apply Date Range");
        applyRangeBtn.setOnAction(e -> refreshPayroll());

        HBox filterRow = new HBox(12, new Label("Driver:"), driverCombo,
                new Label("From:"), fromDatePicker,
                new Label("To:"), toDatePicker, applyRangeBtn);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        filterRow.setPadding(new Insets(10));

        grossLabel = createSummaryLabel();
        serviceFeeLabel = createSummaryLabel();
        grossAfterServiceFeeLabel = createSummaryLabel();
        fuelLabel = createSummaryLabel();
        feesLabel = createSummaryLabel();
        grossAfterFuelLabel = createSummaryLabel();
        driverShareLabel = createSummaryLabel();
        companyShareLabel = createSummaryLabel();
        deductionsLabel = createSummaryLabel();
        netLabel = createSummaryLabel();

        GridPane summaryGrid = new GridPane();
        summaryGrid.setPadding(new Insets(10));
        summaryGrid.setHgap(15);
        summaryGrid.setVgap(8);

        summaryGrid.addRow(0,
                new Label("Gross:"), grossLabel,
                new Label("Service Fee:"), serviceFeeLabel,
                new Label("Gross after Service Fee:"), grossAfterServiceFeeLabel,
                new Label("Fuel:"), fuelLabel,
                new Label("Fees:"), feesLabel
        );
        summaryGrid.addRow(1,
                new Label("Gross after Fuel & Fees:"), grossAfterFuelLabel,
                new Label("Driver Share:"), driverShareLabel,
                new Label("Company Share:"), companyShareLabel,
                new Label("Other Deductions:"), deductionsLabel,
                new Label("Net:"), netLabel
        );

        loadsTable = createLoadsTable();
        fuelTable = createFuelTable();
        feesTable = createFeesTable();
        advancesTable = createAdvancesTable();
        adjustmentsTable = createAdjustmentsTable();

        TabPane detailsTabs = new TabPane(
                new Tab("Loads", loadsTable),
                new Tab("Fuel", fuelTable),
                new Tab("Monthly Fees", withCRUD(feesTable, "MonthlyFee")),
                new Tab("Cash Advances", withCRUD(advancesTable, "CashAdvance")),
                new Tab("Other Adjustments", withCRUD(adjustmentsTable, "OtherAdjustment"))
        );
        detailsTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        VBox mainVBox = new VBox(filterRow, summaryGrid, detailsTabs);
        mainVBox.setSpacing(8);

        setCenter(mainVBox);
    }

    private Label createSummaryLabel() {
        Label l = new Label("$0.00");
        l.setMinWidth(80);
        return l;
    }

    private VBox withCRUD(TableView<?> table, String type) {
        Button addBtn = new Button("Add");
        Button editBtn = new Button("Edit");
        Button delBtn = new Button("Delete");
        HBox hbox = new HBox(8, addBtn, editBtn, delBtn);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 0, 5, 0));

        if ("MonthlyFee".equals(type)) {
            addBtn.setOnAction(e -> addOrEditMonthlyFee(null));
            editBtn.setOnAction(e -> {
                MonthlyFee selected = (MonthlyFee) feesTable.getSelectionModel().getSelectedItem();
                if (selected != null) addOrEditMonthlyFee(selected);
            });
            delBtn.setOnAction(e -> {
                MonthlyFee selected = (MonthlyFee) feesTable.getSelectionModel().getSelectedItem();
                if (selected != null) deleteMonthlyFee(selected);
            });
        } else if ("CashAdvance".equals(type)) {
            addBtn.setOnAction(e -> addOrEditCashAdvance(null));
            editBtn.setOnAction(e -> {
                CashAdvance selected = (CashAdvance) advancesTable.getSelectionModel().getSelectedItem();
                if (selected != null) addOrEditCashAdvance(selected);
            });
            delBtn.setOnAction(e -> {
                CashAdvance selected = (CashAdvance) advancesTable.getSelectionModel().getSelectedItem();
                if (selected != null) deleteCashAdvance(selected);
            });
        } else if ("OtherAdjustment".equals(type)) {
            addBtn.setOnAction(e -> addOrEditAdjustment(null));
            editBtn.setOnAction(e -> {
                OtherDeduction selected = (OtherDeduction) adjustmentsTable.getSelectionModel().getSelectedItem();
                if (selected != null) addOrEditAdjustment(selected);
            });
            delBtn.setOnAction(e -> {
                OtherDeduction selected = (OtherDeduction) adjustmentsTable.getSelectionModel().getSelectedItem();
                if (selected != null) deleteAdjustment(selected);
            });
        }
        VBox box = new VBox(hbox, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return box;
    }

    private TableView<Load> createLoadsTable() {
        TableView<Load> table = new TableView<>(filteredLoads);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Load, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getId()));

        TableColumn<Load, String> deliveredCol = new TableColumn<>("Delivered");
        deliveredCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getDeliveredDate() != null ? cd.getValue().getDeliveredDate().toString() : ""
        ));

        TableColumn<Load, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCustomer()));

        TableColumn<Load, Number> grossCol = new TableColumn<>("Gross");
        grossCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getGrossAmount()));

        TableColumn<Load, Number> driverPercentCol = new TableColumn<>("Driver %");
        driverPercentCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getDriverPercent()));

        TableColumn<Load, Number> driverAmountCol = new TableColumn<>("Driver Amount");
        driverAmountCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(
                cd.getValue().getGrossAmount() * cd.getValue().getDriverPercent() / 100.0
        ));

        table.getColumns().addAll(idCol, deliveredCol, customerCol, grossCol, driverPercentCol, driverAmountCol);
        table.setPlaceholder(new Label("No loads for this period."));
        return table;
    }

    private TableView<FuelTransaction> createFuelTable() {
        TableView<FuelTransaction> table = new TableView<>(filteredFuels);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<FuelTransaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTranDate()));

        TableColumn<FuelTransaction, String> vendorCol = new TableColumn<>("Vendor");
        vendorCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getLocationName()));

        TableColumn<FuelTransaction, String> gallonsCol = new TableColumn<>("Gallons");
        gallonsCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getQty()));

        TableColumn<FuelTransaction, String> totalCostCol = new TableColumn<>("Total Cost");
        totalCostCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAmt()));

        table.getColumns().addAll(dateCol, vendorCol, gallonsCol, totalCostCol);
        table.setPlaceholder(new Label("No fuel charges for this period."));
        return table;
    }

    private TableView<MonthlyFee> createFeesTable() {
        TableView<MonthlyFee> table = new TableView<>(filteredFees);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<MonthlyFee, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getId()));

        TableColumn<MonthlyFee, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFeeType()));

        TableColumn<MonthlyFee, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getDueDate() != null ? cd.getValue().getDueDate().toString() : ""
        ));

        TableColumn<MonthlyFee, String> endDateCol = new TableColumn<>("End Date");
        endDateCol.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getEndDate() != null ? cd.getValue().getEndDate().toString() : ""
        ));

        TableColumn<MonthlyFee, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAmount() != null ? cd.getValue().getAmount().toString() : "0.00"));

        TableColumn<MonthlyFee, String> weeklyCol = new TableColumn<>("Weekly Fee");
        weeklyCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getWeeklyFee() != null ? cd.getValue().getWeeklyFee().toString() : "0.00"));

        TableColumn<MonthlyFee, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNotes()));

        table.getColumns().addAll(idCol, typeCol, dueDateCol, endDateCol, amountCol, weeklyCol, notesCol);
        table.setPlaceholder(new Label("No monthly fees for this period."));
        return table;
    }

    private TableView<CashAdvance> createAdvancesTable() {
        TableView<CashAdvance> table = new TableView<>(filteredAdvances);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<CashAdvance, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getId()));

        TableColumn<CashAdvance, String> issuedDateCol = new TableColumn<>("Issued Date");
        issuedDateCol.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getIssuedDate() != null ? cd.getValue().getIssuedDate().toString() : ""
        ));

        TableColumn<CashAdvance, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNotes()));

        TableColumn<CashAdvance, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAmount() != null ? cd.getValue().getAmount().toString() : "0.00"));

        TableColumn<CashAdvance, String> weeklyCol = new TableColumn<>("Weekly Repayment");
        weeklyCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getWeeklyRepayment() != null ? cd.getValue().getWeeklyRepayment().toString() : "0.00"));

        table.getColumns().addAll(idCol, issuedDateCol, noteCol, amountCol, weeklyCol);
        table.setPlaceholder(new Label("No cash advances for this period."));
        return table;
    }

    private TableView<OtherDeduction> createAdjustmentsTable() {
        TableView<OtherDeduction> table = new TableView<>(filteredAdjustments);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<OtherDeduction, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getId()));

        TableColumn<OtherDeduction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getDate() != null ? cd.getValue().getDate().toString() : ""
        ));

        TableColumn<OtherDeduction, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getReason()));

        TableColumn<OtherDeduction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAmount() != 0.0 ? String.valueOf(cd.getValue().getAmount()) : "0.00"));

        TableColumn<OtherDeduction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getReimbursed() == 1 ? "Reimbursement" : "Deduction"));

        table.getColumns().addAll(idCol, dateCol, reasonCol, amountCol, typeCol);
        table.setPlaceholder(new Label("No adjustments for this period."));
        return table;
    }

    private void addOrEditMonthlyFee(MonthlyFee fee) {
        Stage stage = (Stage) this.getScene().getWindow();
        MonthlyFeeDialog dialog = new MonthlyFeeDialog(stage, fee);
        dialog.showAndWait();
        refreshPayroll();
    }

    private void deleteMonthlyFee(MonthlyFee fee) {
        if (confirmDelete()) {
            monthlyFeeDao.deleteMonthlyFee(fee.getId());
            refreshPayroll();
        }
    }

    private void addOrEditCashAdvance(CashAdvance ca) {
        Stage stage = (Stage) this.getScene().getWindow();
        CashAdvanceDialog dialog = new CashAdvanceDialog(stage, ca);
        dialog.showAndWait();
        refreshPayroll();
    }

    private void deleteCashAdvance(CashAdvance ca) {
        if (confirmDelete()) {
            cashAdvanceDao.deleteCashAdvance(ca.getId());
            refreshPayroll();
        }
    }

    private void addOrEditAdjustment(OtherDeduction adj) {
        Stage stage = (Stage) this.getScene().getWindow();
        OtherAdjustmentDialog dialog = new OtherAdjustmentDialog(stage);
        dialog.showAndWait();
        refreshPayroll();
    }

    private void deleteAdjustment(OtherDeduction adj) {
        if (confirmDelete()) {
            otherDeductionDao.deleteDeduction(adj.getId());
            refreshPayroll();
        }
    }

    private boolean confirmDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this record?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Delete");
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private void selectFirstDriverIfNeeded() {
        if (!driverList.isEmpty() && (driverCombo.getValue() == null || !driverList.contains(driverCombo.getValue()))) {
            driverCombo.setValue(driverList.get(0));
        }
    }

    private void refreshPayroll() {
        Driver driver = driverCombo.getValue();
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();
        if (driver == null || from == null || to == null) {
            clearSummary();
            filteredLoads.clear();
            filteredFuels.clear();
            filteredFees.clear();
            filteredAdvances.clear();
            filteredAdjustments.clear();
            return;
        }

        List<Load> driverLoads = loadDao.getLoadsByDriverAndDateRange(driver.getId(), from, to);
        filteredLoads.setAll(driverLoads);

        List<FuelTransaction> driverFuels = fuelTransactionDao.getFuelTransactionsByDriverNameAndDateRange(driver.getName(), from, to);
        filteredFuels.setAll(driverFuels);

        List<MonthlyFee> driverFees = monthlyFeeDao.getActiveMonthlyFeesForPeriod(driver.getId(), from, to);
        filteredFees.setAll(driverFees);

        List<CashAdvance> driverAdvances = cashAdvanceDao.getCashAdvancesByDriverAndDateRange(driver.getId(), from, to);
        filteredAdvances.setAll(driverAdvances);

        List<OtherDeduction> driverAdjustments = otherDeductionDao.getOtherDeductionsByDriverAndDateRange(driver.getId(), from, to);
        filteredAdjustments.setAll(driverAdjustments);

        calculatePayroll(driver, driverLoads, driverFuels, driverFees, driverAdvances, driverAdjustments);
    }

    private void calculatePayroll(
            Driver driver,
            List<Load> driverLoads,
            List<FuelTransaction> driverFuels,
            List<MonthlyFee> driverFees,
            List<CashAdvance> driverAdvances,
            List<OtherDeduction> driverAdjustments
    ) {
        double gross = driverLoads.stream().mapToDouble(Load::getGrossAmount).sum();
        double serviceFee = gross * (driver.getCompanyServiceFeePercent() / 100.0);
        double grossAfterServiceFee = gross - serviceFee;
        double fuel = driverFuels.stream().mapToDouble(f -> parseDoubleSafe(f.getAmt())).sum();

        double fees = driverFuels.stream().mapToDouble(f -> parseDoubleSafe(f.getFees())).sum();

        double monthlyFees = driverFees.stream()
            .mapToDouble(fee -> fee.getWeeklyFee() != null ? fee.getWeeklyFee().doubleValue() : 0.0)
            .sum();

        double grossAfterFuel = grossAfterServiceFee - fuel - fees - monthlyFees;

        double driverShare = grossAfterFuel * (driver.getDriverPercent() / 100.0);
        double companyShare = grossAfterFuel * (driver.getCompanyPercent() / 100.0);

        double otherDeductions = driverAdjustments.stream()
                .filter(a -> a.getReimbursed() == 0)
                .mapToDouble(OtherDeduction::getAmount)
                .sum();
        double reimbursements = driverAdjustments.stream()
                .filter(a -> a.getReimbursed() == 1)
                .mapToDouble(OtherDeduction::getAmount)
                .sum();

        double cashAdvanceRepayment = driverAdvances.stream()
                .mapToDouble(a -> a.getWeeklyRepayment() != null ? a.getWeeklyRepayment().doubleValue() : 0.0)
                .sum();

        double net = driverShare - otherDeductions - cashAdvanceRepayment + reimbursements;

        grossLabel.setText(String.format("$%,.2f", gross));
        serviceFeeLabel.setText(String.format("$%,.2f", serviceFee));
        grossAfterServiceFeeLabel.setText(String.format("$%,.2f", grossAfterServiceFee));
        fuelLabel.setText(String.format("$%,.2f", fuel));
        feesLabel.setText(String.format("$%,.2f", fees + monthlyFees));
        grossAfterFuelLabel.setText(String.format("$%,.2f", grossAfterFuel));
        driverShareLabel.setText(String.format("$%,.2f", driverShare));
        companyShareLabel.setText(String.format("$%,.2f", companyShare));
        deductionsLabel.setText(String.format("$%,.2f", otherDeductions + cashAdvanceRepayment));
        netLabel.setText(String.format("$%,.2f", net));
    }

    private double parseDoubleSafe(String val) {
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void clearSummary() {
        grossLabel.setText("$0.00");
        serviceFeeLabel.setText("$0.00");
        grossAfterServiceFeeLabel.setText("$0.00");
        fuelLabel.setText("$0.00");
        feesLabel.setText("$0.00");
        grossAfterFuelLabel.setText("$0.00");
        driverShareLabel.setText("$0.00");
        companyShareLabel.setText("$0.00");
        deductionsLabel.setText("$0.00");
        netLabel.setText("$0.00");
    }

    // --- DataRefreshListener implementation ---
    @Override
    public void refreshDrivers() {
        DriverRepository.getInstance().refreshDriversFromDatabase();
        // Payroll will refresh due to driverList listener
    }

    @Override
    public void refreshLoads() {
        refreshPayroll();
    }
}