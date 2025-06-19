package com.company.payroll.ui;

import com.company.payroll.dao.DriverDao;
import com.company.payroll.model.Driver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmployeeTab extends BorderPane implements DataRefreshListener {
    private final TableView<Driver> table;
    private final ObservableList<Driver> data;
    private final DriverDao driverDao = new DriverDao();

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Listeners to notify on data change (e.g. LoadsTab)
    private final List<DataRefreshListener> dataRefreshListeners = new ArrayList<>();

    public EmployeeTab() {
        data = FXCollections.observableArrayList(driverDao.getAllDrivers());
        table = new TableView<>(data);

        TableColumn<Driver, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Driver, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Driver, String> truckCol = new TableColumn<>("Truck/Unit");
        truckCol.setCellValueFactory(new PropertyValueFactory<>("truckId"));

        TableColumn<Driver, Double> driverPercentCol = new TableColumn<>("Driver %");
        driverPercentCol.setCellValueFactory(new PropertyValueFactory<>("driverPercent"));

        TableColumn<Driver, Boolean> fuelDiscountCol = new TableColumn<>("Fuel Discount");
        fuelDiscountCol.setCellValueFactory(new PropertyValueFactory<>("fuelDiscountEligible"));

        TableColumn<Driver, Double> companyPercentCol = new TableColumn<>("Company %");
        companyPercentCol.setCellValueFactory(new PropertyValueFactory<>("companyPercent"));

        TableColumn<Driver, Double> serviceFeeCol = new TableColumn<>("Service Fee %");
        serviceFeeCol.setCellValueFactory(new PropertyValueFactory<>("companyServiceFeePercent"));

        TableColumn<Driver, String> driveTypeCol = new TableColumn<>("Drive Type");
        driveTypeCol.setCellValueFactory(new PropertyValueFactory<>("driveType"));

        TableColumn<Driver, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<Driver, String> licenseNumberCol = new TableColumn<>("License #");
        licenseNumberCol.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));

        TableColumn<Driver, String> driversLLCCol = new TableColumn<>("Drivers LLC");
        driversLLCCol.setCellValueFactory(new PropertyValueFactory<>("driversLLC"));

        TableColumn<Driver, LocalDate> cdlExpiryCol = new TableColumn<>("CDL Expiry");
        cdlExpiryCol.setCellValueFactory(new PropertyValueFactory<>("cdlExpiry"));
        cdlExpiryCol.setCellFactory(col -> getExpiryCell());

        TableColumn<Driver, LocalDate> medExpiryCol = new TableColumn<>("Medical Expiry");
        medExpiryCol.setCellValueFactory(new PropertyValueFactory<>("medicalExpiry"));
        medExpiryCol.setCellFactory(col -> getExpiryCell());

        // --- ADD THIS COLUMN FOR ACTIVE ---
        TableColumn<Driver, Boolean> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        // Optionally, display as "Yes"/"No":
        /*
        activeCol.setCellFactory(tc -> new TableCell<Driver, Boolean>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                setText(empty ? "" : (active != null && active ? "Yes" : "No"));
            }
        });
        */
        // --- END ADD ---

        table.getColumns().addAll(
                idCol, nameCol, truckCol, driverPercentCol, fuelDiscountCol, companyPercentCol, serviceFeeCol, driveTypeCol, phoneCol,
                licenseNumberCol, driversLLCCol, cdlExpiryCol, medExpiryCol,
                activeCol // <-- ADD COLUMN TO TABLE
        );

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> showDriverDialog(null));

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> {
            Driver d = table.getSelectionModel().getSelectedItem();
            if (d != null) showDriverDialog(d);
        });

        Button delBtn = new Button("Delete");
        delBtn.setOnAction(e -> {
            Driver d = table.getSelectionModel().getSelectedItem();
            if (d != null) {
                driverDao.deleteDriver(d.getId());
                refresh();
                notifyDataRefresh();
            }
        });

        HBox controls = new HBox(10, addBtn, editBtn, delBtn);
        controls.setPadding(new Insets(10));

        setCenter(table);
        setBottom(controls);
    }

    public void addDataRefreshListener(DataRefreshListener listener) {
        if (!dataRefreshListeners.contains(listener))
            dataRefreshListeners.add(listener);
    }

    private void notifyDataRefresh() {
        for (DataRefreshListener listener : dataRefreshListeners) {
            listener.refreshDrivers();
            listener.refreshLoads(); // <-- ADD THIS LINE!
        }
    }

    private TableCell<Driver, LocalDate> getExpiryCell() {
        return new TableCell<Driver, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText("");
                    setStyle("");
                } else {
                    setText(dtf.format(date));
                    LocalDate now = LocalDate.now();
                    if (date.isBefore(now)) {
                        setStyle("-fx-background-color: #ffcccc;"); // Red for expired
                    } else if (!date.isAfter(now.plusDays(30))) {
                        setStyle("-fx-background-color: #fff2cc;"); // Yellow for near expiry
                    } else {
                        setStyle("");
                    }
                }
            }
        };
    }

    private void showDriverDialog(Driver driver) {
        Stage dialog = new Stage();
        dialog.setTitle(driver == null ? "Add Driver/Employee" : "Edit Driver/Employee");

        TextField nameField = new TextField(driver == null ? "" : driver.getName());
        TextField truckField = new TextField(driver == null ? "" : driver.getTruckId());
        TextField driverPercentField = new TextField(driver == null ? "60.00" : String.valueOf(driver.getDriverPercent()));
        CheckBox fuelDiscountBox = new CheckBox("Fuel Discount Eligible");
        fuelDiscountBox.setSelected(driver != null && driver.isFuelDiscountEligible());
        TextField companyPercentField = new TextField(driver == null ? "40.00" : String.valueOf(driver.getCompanyPercent()));
        TextField serviceFeeField = new TextField(driver == null ? "0.00" : String.valueOf(driver.getCompanyServiceFeePercent()));
        ComboBox<String> driveTypeCombo = new ComboBox<>(FXCollections.observableArrayList("Owner Operator", "Company Driver"));
        driveTypeCombo.setValue(driver == null ? "Owner Operator" : driver.getDriveType());
        TextField phoneField = new TextField(driver == null ? "" : driver.getPhoneNumber());
        DatePicker cdlExpiryPicker = new DatePicker(driver == null ? null : driver.getCdlExpiry());
        DatePicker medicalExpiryPicker = new DatePicker(driver == null ? null : driver.getMedicalExpiry());
        TextField licenseNumberField = new TextField(driver == null ? "" : driver.getLicenseNumber());
        TextField driversLLCField = new TextField(driver == null ? "" : driver.getDriversLLC());

        // --- ADD THIS FOR ACTIVE FIELD ---
        CheckBox activeCheckBox = new CheckBox("Active");
        activeCheckBox.setSelected(driver != null && driver.isActive());
        // --- END ADD ---

        // Color coding for DatePickers
        cdlExpiryPicker.valueProperty().addListener((obs, ov, nv) -> updateDateColor(cdlExpiryPicker));
        medicalExpiryPicker.valueProperty().addListener((obs, ov, nv) -> updateDateColor(medicalExpiryPicker));
        updateDateColor(cdlExpiryPicker);
        updateDateColor(medicalExpiryPicker);

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                if (nameField.getText().trim().isEmpty()) {
                    showAlert("Name is required."); return;
                }
                if (cdlExpiryPicker.getValue() == null) {
                    showAlert("CDL Expiry is required."); return;
                }
                if (medicalExpiryPicker.getValue() == null) {
                    showAlert("Medical Expiry is required."); return;
                }
                if (driver == null) {
                    Driver d = new Driver();
                    d.setName(nameField.getText());
                    d.setTruckId(truckField.getText());
                    d.setDriverPercent(Double.parseDouble(driverPercentField.getText()));
                    d.setFuelDiscountEligible(fuelDiscountBox.isSelected());
                    d.setCompanyPercent(Double.parseDouble(companyPercentField.getText()));
                    d.setCompanyServiceFeePercent(Double.parseDouble(serviceFeeField.getText()));
                    d.setDriveType(driveTypeCombo.getValue());
                    d.setPhoneNumber(phoneField.getText());
                    d.setCdlExpiry(cdlExpiryPicker.getValue());
                    d.setMedicalExpiry(medicalExpiryPicker.getValue());
                    d.setLicenseNumber(licenseNumberField.getText());
                    d.setDriversLLC(driversLLCField.getText());
                    // --- ADD THIS ---
                    d.setActive(activeCheckBox.isSelected());
                    // --- END ADD ---
                    driverDao.addDriver(d);
                } else {
                    driver.setName(nameField.getText());
                    driver.setTruckId(truckField.getText());
                    driver.setDriverPercent(Double.parseDouble(driverPercentField.getText()));
                    driver.setFuelDiscountEligible(fuelDiscountBox.isSelected());
                    driver.setCompanyPercent(Double.parseDouble(companyPercentField.getText()));
                    driver.setCompanyServiceFeePercent(Double.parseDouble(serviceFeeField.getText()));
                    driver.setDriveType(driveTypeCombo.getValue());
                    driver.setPhoneNumber(phoneField.getText());
                    driver.setCdlExpiry(cdlExpiryPicker.getValue());
                    driver.setMedicalExpiry(medicalExpiryPicker.getValue());
                    driver.setLicenseNumber(licenseNumberField.getText());
                    driver.setDriversLLC(driversLLCField.getText());
                    // --- ADD THIS ---
                    driver.setActive(activeCheckBox.isSelected());
                    // --- END ADD ---
                    driverDao.updateDriver(driver);
                }
                refresh();
                notifyDataRefresh();
                dialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Invalid input: " + ex.getMessage());
            }
        });

        VBox vbox = new VBox(10,
                new Label("Driver Name:"), nameField,
                new Label("Truck/Unit:"), truckField,
                new Label("Driver % (Earned by Driver):"), driverPercentField,
                fuelDiscountBox,
                new Label("Company % (Earned by Company):"), companyPercentField,
                new Label("Company Service Fee %:"), serviceFeeField,
                new Label("Drive Type:"), driveTypeCombo,
                new Label("Phone Number:"), phoneField,
                new Label("CDL Expiry:"), cdlExpiryPicker,
                new Label("Medical Expiry:"), medicalExpiryPicker,
                new Label("License Number:"), licenseNumberField,
                new Label("Drivers LLC:"), driversLLCField,
                // --- ADD THIS ---
                activeCheckBox,
                // --- END ADD ---
                saveBtn
        );
        vbox.setPadding(new Insets(20));
        dialog.setScene(new Scene(vbox));
        dialog.show();
    }

    private void updateDateColor(DatePicker datePicker) {
        LocalDate date = datePicker.getValue();
        if (date == null) {
            datePicker.setStyle("");
            return;
        }
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            datePicker.setStyle("-fx-control-inner-background: #ffcccc;"); // Red
        } else if (!date.isAfter(today.plusDays(30))) {
            datePicker.setStyle("-fx-control-inner-background: #fff2cc;"); // Yellow
        } else {
            datePicker.setStyle("");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Validation Error");
        alert.showAndWait();
    }

    private void refresh() {
        data.setAll(driverDao.getAllDrivers());
    }

    // DataRefreshListener interface methods
    @Override
    public void refreshDrivers() {
        refresh();
    }

    @Override
    public void refreshLoads() {
        // No-op for EmployeeTab, unless you want to auto-refresh a loads table here in the future
    }
}