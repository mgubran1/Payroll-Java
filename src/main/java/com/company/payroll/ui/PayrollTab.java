package com.company.payroll.ui;

import com.company.payroll.dao.DriverDao;
import com.company.payroll.dao.LoadDao;
import com.company.payroll.dao.FuelTransactionDao;
import com.company.payroll.dao.MonthlyFeeDao;
import com.company.payroll.dao.CashAdvanceDao;
import com.company.payroll.dao.OtherDeductionDao;
import com.company.payroll.model.Driver;
import com.company.payroll.model.Load;
import com.company.payroll.model.FuelTransaction;
import com.company.payroll.model.MonthlyFee;
import com.company.payroll.model.CashAdvance;
import com.company.payroll.model.OtherDeduction;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PayrollTab extends BorderPane {

    private final DriverDao driverDao = new DriverDao();
    private final LoadDao loadDao = new LoadDao();
    private final FuelTransactionDao fuelTransactionDao = new FuelTransactionDao();
    private final MonthlyFeeDao monthlyFeeDao = new MonthlyFeeDao();
    private final CashAdvanceDao cashAdvanceDao = new CashAdvanceDao();
    private final OtherDeductionDao otherDeductionDao = new OtherDeductionDao();

    private final ObservableList<Driver> driverList = FXCollections.observableArrayList();
    private final ObservableList<Load> filteredLoads = FXCollections.observableArrayList();
    private final ObservableList<FuelTransaction> filteredFuels = FXCollections.observableArrayList();
    private final ObservableList<MonthlyFee> filteredFees = FXCollections.observableArrayList();
    private final ObservableList<CashAdvance> filteredAdvances = FXCollections.observableArrayList();
    private final ObservableList<OtherDeduction> filteredAdjustments = FXCollections.observableArrayList();

    private TextField paidFromField;
    private Spinner<Integer> yearSpinner;
    private ComboBox<Driver> driverCombo;
    private DatePicker fromDatePicker, toDatePicker;

    // Payroll summary labels
    private Label grossLabel, serviceFeeLabel, grossAfterServiceFeeLabel, fuelLabel,
            grossAfterFuelLabel, driverShareLabel, companyShareLabel, deductionsLabel, netLabel;

    private TableView<Load> loadsTable;
    private TableView<FuelTransaction> fuelTable;
    private TableView<MonthlyFee> feesTable;
    private TableView<CashAdvance> advancesTable;
    private TableView<OtherDeduction> adjustmentsTable;

    public PayrollTab() {
        // Top: Paid From and Year
        paidFromField = new TextField();
        paidFromField.setPromptText("e.g. Windy City Transport LLC");

        yearSpinner = new Spinner<>(2000, 2100, LocalDate.now().getYear());
        yearSpinner.setEditable(true);

        HBox topRow = new HBox(15,
                new Label("Paid From:"), paidFromField,
                new Label("Year:"), yearSpinner
        );
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setPadding(new Insets(10));

        // Driver and date range selection
        driverCombo = new ComboBox<>(driverList);
        driverCombo.setPromptText("Select Driver");
        driverCombo.setMinWidth(200);

        fromDatePicker = new DatePicker();
        fromDatePicker.setPromptText("From");
        toDatePicker = new DatePicker();
        toDatePicker.setPromptText("To");

        Button refreshBtn = new Button("Search");
        refreshBtn.setOnAction(e -> refreshPayroll());

        HBox selectionRow = new HBox(15,
                new Label("Driver:"), driverCombo,
                new Label("From:"), fromDatePicker,
                new Label("To:"), toDatePicker,
                refreshBtn
        );
        selectionRow.setAlignment(Pos.CENTER_LEFT);
        selectionRow.setPadding(new Insets(0, 10, 10, 10));

        // Payroll summary grid
        grossLabel = makeSummaryLabel();
        serviceFeeLabel = makeSummaryLabel();
        grossAfterServiceFeeLabel = makeSummaryLabel();
        fuelLabel = makeSummaryLabel();
        grossAfterFuelLabel = makeSummaryLabel();
        driverShareLabel = makeSummaryLabel();
        companyShareLabel = makeSummaryLabel();
        deductionsLabel = makeSummaryLabel();
        netLabel = makeSummaryLabel();

        GridPane summaryGrid = new GridPane();
        summaryGrid.setPadding(new Insets(10));
        summaryGrid.setHgap(15);
        summaryGrid.setVgap(8);

        summaryGrid.addRow(0,
                new Label("Gross:"), grossLabel,
                new Label("Service Fee:"), serviceFeeLabel,
                new Label("Gross after Service Fee:"), grossAfterServiceFeeLabel,
                new Label("Fuel:"), fuelLabel
        );
        summaryGrid.addRow(1,
                new Label("Gross after Fuel:"), grossAfterFuelLabel,
                new Label("Driver Share:"), driverShareLabel,
                new Label("Company Share:"), companyShareLabel,
                new Label("Other Deductions:"), deductionsLabel,
                new Label("Net:"), netLabel
        );

        // Tabbed details section
        loadsTable = createLoadsTable();
        fuelTable = createFuelTable();
        feesTable = createFeesTable();
        advancesTable = createAdvancesTable();
        adjustmentsTable = createAdjustmentsTable();

        TabPane detailsTabs = new TabPane();
        detailsTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        detailsTabs.getTabs().addAll(
                new Tab("Loads", loadsTable),
                new Tab("Fuel", fuelTable),
                new Tab("Monthly Fees", feesTable),
                new Tab("Cash Advances", advancesTable),
                new Tab("Other Adjustments", adjustmentsTable)
        );

        // Bottom buttons
        HBox buttonsRow = new HBox(15,
                new Button("Save Payroll"),
                new Button("Preview Paystub"),
                new Button("Payroll History"),
                new Button("Export Payroll PDF")
        );
        buttonsRow.setAlignment(Pos.CENTER);
        buttonsRow.setPadding(new Insets(10));

        VBox mainVBox = new VBox(topRow, selectionRow, summaryGrid, detailsTabs, buttonsRow);
        mainVBox.setSpacing(8);

        setCenter(mainVBox);

        // Load drivers
        refreshDrivers();
    }

    private Label makeSummaryLabel() {
        Label label = new Label("$0.00");
        label.setMinWidth(80);
        return label;
    }

    private TableView<Load> createLoadsTable() {
        TableView<Load> table = new TableView<>(filteredLoads);

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
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private TableView<FuelTransaction> createFuelTable() {
        TableView<FuelTransaction> table = new TableView<>(filteredFuels);

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
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private TableView<MonthlyFee> createFeesTable() {
        TableView<MonthlyFee> table = new TableView<>(filteredFees);

        TableColumn<MonthlyFee, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getId()));

        TableColumn<MonthlyFee, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFeeType()));

        TableColumn<MonthlyFee, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getDueDate() != null ? cd.getValue().getDueDate().toString() : ""
        ));

        TableColumn<MonthlyFee, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAmount() != null ? cd.getValue().getAmount().toString() : "0.00"));

        table.getColumns().addAll(idCol, typeCol, dueDateCol, amountCol);
        table.setPlaceholder(new Label("No monthly fees for this period."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private TableView<CashAdvance> createAdvancesTable() {
        TableView<CashAdvance> table = new TableView<>(filteredAdvances);

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

        table.getColumns().addAll(idCol, issuedDateCol, noteCol, amountCol);
        table.setPlaceholder(new Label("No cash advances for this period."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private TableView<OtherDeduction> createAdjustmentsTable() {
        TableView<OtherDeduction> table = new TableView<>(filteredAdjustments);

        TableColumn<OtherDeduction, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getId()));

        TableColumn<OtherDeduction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getDate() != null ? cd.getValue().getDate().toString() : ""
        ));

        TableColumn<OtherDeduction, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getReason()));

        TableColumn<OtherDeduction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAmount() != null ? cd.getValue().getAmount().toString() : "0.00"));

        table.getColumns().addAll(idCol, dateCol, reasonCol, amountCol);
        table.setPlaceholder(new Label("No adjustments for this period."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private void refreshDrivers() {
        List<Driver> drivers = driverDao.getAllDrivers();
        driverList.setAll(drivers);
    }

    // Call this when a user searches for payroll for a driver & date range
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

        // Filter loads for driver and date range
        List<Load> allLoads = loadDao.getAllLoads();
        List<Load> driverLoads = allLoads.stream()
                .filter(l -> l.getDriverId() == driver.getId()
                        && l.getDeliveredDate() != null
                        && !l.getDeliveredDate().isBefore(from)
                        && !l.getDeliveredDate().isAfter(to))
                .collect(Collectors.toList());
        filteredLoads.setAll(driverLoads);

        // Use robust alias method for FuelTransactionDao
        List<FuelTransaction> allFuels = fuelTransactionDao.getAllFuelTransactions();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<FuelTransaction> driverFuels = allFuels.stream()
                .filter(f -> f.getDriverName() != null && f.getDriverName().equals(driver.getName())
                        && f.getTranDate() != null
                        && isDateInRange(f.getTranDate(), from, to, dateFormatter))
                .collect(Collectors.toList());
        filteredFuels.setAll(driverFuels);

        // Filter fees, advances, adjustments
        List<MonthlyFee> allFees = monthlyFeeDao.getAllMonthlyFees();
        List<MonthlyFee> driverFees = allFees.stream()
                .filter(f -> f.getDriverId() == driver.getId()
                        && f.getDueDate() != null
                        && !f.getDueDate().isBefore(from)
                        && !f.getDueDate().isAfter(to))
                .collect(Collectors.toList());
        filteredFees.setAll(driverFees);

        List<CashAdvance> allAdvances = cashAdvanceDao.getAllCashAdvances();
        List<CashAdvance> driverAdvances = allAdvances.stream()
                .filter(a -> a.getDriverId() == driver.getId()
                        && a.getIssuedDate() != null
                        && !a.getIssuedDate().isBefore(from)
                        && !a.getIssuedDate().isAfter(to))
                .collect(Collectors.toList());
        filteredAdvances.setAll(driverAdvances);

        List<OtherDeduction> allAdjustments = otherDeductionDao.getAllOtherDeductions();
        List<OtherDeduction> driverAdjustments = allAdjustments.stream()
                .filter(a -> a.getDriverId() == driver.getId()
                        && a.getDate() != null
                        && !a.getDate().isBefore(from)
                        && !a.getDate().isAfter(to))
                .collect(Collectors.toList());
        filteredAdjustments.setAll(driverAdjustments);

        // Payroll calculations
        double gross = driverLoads.stream().mapToDouble(Load::getGrossAmount).sum();
        double serviceFee = driverLoads.stream()
                .mapToDouble(l -> l.getGrossAmount() * (driver.getCompanyServiceFeePercent() / 100.0))
                .sum();
        double grossAfterServiceFee = gross - serviceFee;
        double fuel = driverFuels.stream().mapToDouble(f -> parseDoubleSafe(f.getAmt())).sum();
        double grossAfterFuel = grossAfterServiceFee - fuel;
        double driverShare = driverLoads.stream()
                .mapToDouble(l -> l.getGrossAmount() * (l.getDriverPercent() / 100.0))
                .sum();
        double companyShare = grossAfterFuel - driverShare;
        double feeDeductions = driverFees.stream().mapToDouble(f -> f.getAmount() != null ? f.getAmount().doubleValue() : 0.0).sum();
        double advancesDeductions = driverAdvances.stream().mapToDouble(a -> a.getAmount() != null ? a.getAmount().doubleValue() : 0.0).sum();
        double adjustmentDeductions = driverAdjustments.stream().mapToDouble(a -> a.getAmount() != null ? a.getAmount().doubleValue() : 0.0).sum();
        double otherDeductions = feeDeductions + advancesDeductions + adjustmentDeductions;
        double net = driverShare - otherDeductions;

        // Update summary labels
        grossLabel.setText(String.format("$%,.2f", gross));
        serviceFeeLabel.setText(String.format("$%,.2f", serviceFee));
        grossAfterServiceFeeLabel.setText(String.format("$%,.2f", grossAfterServiceFee));
        fuelLabel.setText(String.format("$%,.2f", fuel));
        grossAfterFuelLabel.setText(String.format("$%,.2f", grossAfterFuel));
        driverShareLabel.setText(String.format("$%,.2f", driverShare));
        companyShareLabel.setText(String.format("$%,.2f", companyShare));
        deductionsLabel.setText(String.format("$%,.2f", otherDeductions));
        netLabel.setText(String.format("$%,.2f", net));
    }

    private boolean isDateInRange(String dateStr, LocalDate from, LocalDate to, DateTimeFormatter formatter) {
        try {
            LocalDate d = LocalDate.parse(dateStr, formatter);
            return (d != null && !d.isBefore(from) && !d.isAfter(to));
        } catch (Exception e) {
            return false;
        }
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
        grossAfterFuelLabel.setText("$0.00");
        driverShareLabel.setText("$0.00");
        companyShareLabel.setText("$0.00");
        deductionsLabel.setText("$0.00");
        netLabel.setText("$0.00");
    }
}