package com.company.payroll.ui;

import com.company.payroll.dao.DriverDao;
import com.company.payroll.dao.LoadDao;
import com.company.payroll.dao.FuelDao;
import com.company.payroll.dao.FeeDao;
import com.company.payroll.dao.CashAdvanceDao;
import com.company.payroll.dao.AdjustmentDao;
import com.company.payroll.model.Driver;
import com.company.payroll.model.Load;
import com.company.payroll.model.Fuel;
import com.company.payroll.model.Fee;
import com.company.payroll.model.CashAdvance;
import com.company.payroll.model.Adjustment;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PayrollTab extends BorderPane {

    private final DriverDao driverDao = new DriverDao();
    private final LoadDao loadDao = new LoadDao();
    private final FuelDao fuelDao = new FuelDao();
    private final FeeDao feeDao = new FeeDao();
    private final CashAdvanceDao cashAdvanceDao = new CashAdvanceDao();
    private final AdjustmentDao adjustmentDao = new AdjustmentDao();

    private final ObservableList<Driver> driverList = FXCollections.observableArrayList();
    private final ObservableList<Load> filteredLoads = FXCollections.observableArrayList();
    private final ObservableList<Fuel> filteredFuels = FXCollections.observableArrayList();
    private final ObservableList<Fee> filteredFees = FXCollections.observableArrayList();
    private final ObservableList<CashAdvance> filteredAdvances = FXCollections.observableArrayList();
    private final ObservableList<Adjustment> filteredAdjustments = FXCollections.observableArrayList();

    private TextField paidFromField;
    private Spinner<Integer> yearSpinner;
    private ComboBox<Driver> driverCombo;
    private DatePicker fromDatePicker, toDatePicker;

    // Payroll summary labels
    private Label grossLabel, serviceFeeLabel, grossAfterServiceFeeLabel, fuelLabel,
            grossAfterFuelLabel, driverShareLabel, companyShareLabel, deductionsLabel, netLabel;

    private TableView<Load> loadsTable;
    private TableView<Fuel> fuelTable;
    private TableView<Fee> feesTable;
    private TableView<CashAdvance> advancesTable;
    private TableView<Adjustment> adjustmentsTable;

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

    private TableView<Fuel> createFuelTable() {
        TableView<Fuel> table = new TableView<>(filteredFuels);

        TableColumn<Fuel, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getId()));

        TableColumn<Fuel, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getDate() != null ? cd.getValue().getDate().toString() : ""
        ));

        TableColumn<Fuel, String> vendorCol = new TableColumn<>("Vendor");
        vendorCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getVendor()));

        TableColumn<Fuel, Number> gallonsCol = new TableColumn<>("Gallons");
        gallonsCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getGallons()));

        TableColumn<Fuel, Number> totalCostCol = new TableColumn<>("Total Cost");
        totalCostCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getTotalCost()));

        table.getColumns().addAll(idCol, dateCol, vendorCol, gallonsCol, totalCostCol);
        table.setPlaceholder(new Label("No fuel charges for this period."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private TableView<Fee> createFeesTable() {
        TableView<Fee> table = new TableView<>(filteredFees);

        TableColumn<Fee, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getId()));

        TableColumn<Fee, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getType()));

        TableColumn<Fee, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getDate() != null ? cd.getValue().getDate().toString() : ""
        ));

        TableColumn<Fee, Number> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getAmount()));

        table.getColumns().addAll(idCol, typeCol, dateCol, amountCol);
        table.setPlaceholder(new Label("No monthly fees for this period."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private TableView<CashAdvance> createAdvancesTable() {
        TableView<CashAdvance> table = new TableView<>(filteredAdvances);

        TableColumn<CashAdvance, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getId()));

        TableColumn<CashAdvance, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getDate() != null ? cd.getValue().getDate().toString() : ""
        ));

        TableColumn<CashAdvance, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNote()));

        TableColumn<CashAdvance, Number> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getAmount()));

        table.getColumns().addAll(idCol, dateCol, noteCol, amountCol);
        table.setPlaceholder(new Label("No cash advances for this period."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private TableView<Adjustment> createAdjustmentsTable() {
        TableView<Adjustment> table = new TableView<>(filteredAdjustments);

        TableColumn<Adjustment, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getId()));

        TableColumn<Adjustment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getDate() != null ? cd.getValue().getDate().toString() : ""
        ));

        TableColumn<Adjustment, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getReason()));

        TableColumn<Adjustment, Number> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getAmount()));

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

        // Filter fuel records for driver and date range (adjust as per your Fuel model)
        List<Fuel> allFuels = fuelDao.getAllFuel();
        List<Fuel> driverFuels = allFuels.stream()
                .filter(f -> f.getDriverId() == driver.getId()
                        && f.getDate() != null
                        && !f.getDate().isBefore(from)
                        && !f.getDate().isAfter(to))
                .collect(Collectors.toList());
        filteredFuels.setAll(driverFuels);

        // Filter fees, advances, adjustments
        List<Fee> allFees = feeDao.getAllFees();
        List<Fee> driverFees = allFees.stream()
                .filter(f -> f.getDriverId() == driver.getId()
                        && f.getDate() != null
                        && !f.getDate().isBefore(from)
                        && !f.getDate().isAfter(to))
                .collect(Collectors.toList());
        filteredFees.setAll(driverFees);

        List<CashAdvance> allAdvances = cashAdvanceDao.getAllAdvances();
        List<CashAdvance> driverAdvances = allAdvances.stream()
                .filter(a -> a.getDriverId() == driver.getId()
                        && a.getDate() != null
                        && !a.getDate().isBefore(from)
                        && !a.getDate().isAfter(to))
                .collect(Collectors.toList());
        filteredAdvances.setAll(driverAdvances);

        List<Adjustment> allAdjustments = adjustmentDao.getAllAdjustments();
        List<Adjustment> driverAdjustments = allAdjustments.stream()
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
        double fuel = driverFuels.stream().mapToDouble(Fuel::getTotalCost).sum();
        double grossAfterFuel = grossAfterServiceFee - fuel;
        double driverShare = driverLoads.stream()
                .mapToDouble(l -> l.getGrossAmount() * (l.getDriverPercent() / 100.0))
                .sum();
        double companyShare = grossAfterFuel - driverShare;
        double feeDeductions = driverFees.stream().mapToDouble(Fee::getAmount).sum();
        double advancesDeductions = driverAdvances.stream().mapToDouble(CashAdvance::getAmount).sum();
        double adjustmentDeductions = driverAdjustments.stream().mapToDouble(Adjustment::getAmount).sum();
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