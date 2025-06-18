package com.company.payroll.ui;

import com.company.payroll.model.Load;
import com.company.payroll.model.Driver;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class LoadDialog extends Stage {

    private TextField customerField;
    private DatePicker deliveredDatePicker;
    private TextField grossAmountField;
    private ComboBox<Driver> driverComboBox;
    private TextField driverPercentField;
    private Button saveButton;
    private Button cancelButton;
    private Load load;
    private boolean saved = false;

    public LoadDialog(Stage owner, Load load, java.util.List<Driver> drivers) {
        this.load = load != null ? load : new Load();
        setTitle(load != null ? "Edit Load" : "Add Load");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);

        customerField = new TextField(this.load.getCustomer());
        deliveredDatePicker = new DatePicker(this.load.getDeliveredDate() != null ? this.load.getDeliveredDate() : LocalDate.now());
        grossAmountField = new TextField(this.load.getGrossAmount() != 0 ? String.valueOf(this.load.getGrossAmount()) : "");
        driverComboBox = new ComboBox<>();
        driverComboBox.getItems().addAll(drivers);
        driverComboBox.setValue(this.load.getDriver());

        driverPercentField = new TextField(this.load.getDriverPercent() != 0 ? String.valueOf(this.load.getDriverPercent()) : "");

        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");

        saveButton.setDefaultButton(true);
        cancelButton.setCancelButton(true);

        saveButton.setOnAction(e -> {
            if (validate()) {
                updateLoadFromFields();
                saved = true;
                close();
            }
        });

        cancelButton.setOnAction(e -> close());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Customer:"), 0, 0);
        grid.add(customerField, 1, 0);

        grid.add(new Label("Delivered Date:"), 0, 1);
        grid.add(deliveredDatePicker, 1, 1);

        grid.add(new Label("Gross Amount:"), 0, 2);
        grid.add(grossAmountField, 1, 2);

        grid.add(new Label("Driver:"), 0, 3);
        grid.add(driverComboBox, 1, 3);

        grid.add(new Label("Driver Percent:"), 0, 4);
        grid.add(driverPercentField, 1, 4);

        grid.add(saveButton, 0, 5);
        grid.add(cancelButton, 1, 5);

        setScene(new Scene(grid));
    }

    private boolean validate() {
        String errorMsg = "";
        if (customerField.getText().trim().isEmpty())
            errorMsg += "Customer is required.\n";
        if (deliveredDatePicker.getValue() == null)
            errorMsg += "Delivered date is required.\n";
        if (grossAmountField.getText().trim().isEmpty())
            errorMsg += "Gross amount is required.\n";
        else {
            try {
                Double.parseDouble(grossAmountField.getText().trim());
            } catch (NumberFormatException ex) {
                errorMsg += "Gross amount must be a valid number.\n";
            }
        }
        if (driverComboBox.getValue() == null)
            errorMsg += "Driver is required.\n";
        if (driverPercentField.getText().trim().isEmpty())
            errorMsg += "Driver percent is required.\n";
        else {
            try {
                Double.parseDouble(driverPercentField.getText().trim());
            } catch (NumberFormatException ex) {
                errorMsg += "Driver percent must be a valid number.\n";
            }
        }

        if (!errorMsg.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, errorMsg, ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void updateLoadFromFields() {
        load.setCustomer(customerField.getText().trim());
        load.setDeliveredDate(deliveredDatePicker.getValue());
        load.setGrossAmount(Double.parseDouble(grossAmountField.getText().trim()));
        load.setDriver(driverComboBox.getValue());
        load.setDriverPercent(Double.parseDouble(driverPercentField.getText().trim()));
    }

    public Load getLoad() {
        return load;
    }

    public boolean isSaved() {
        return saved;
    }
}