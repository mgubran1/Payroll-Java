package com.company.payroll.ui;

import com.company.payroll.model.Driver;
import com.company.payroll.model.FuelTransaction;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class FuelTransactionDialog extends Stage {

    private DatePicker datePicker;
    private TextField vendorField;
    private TextField gallonsField;
    private TextField costField;
    private ComboBox<Driver> driverComboBox;
    private Button saveButton;
    private Button cancelButton;
    private FuelTransaction fuelTransaction;
    private boolean saved = false;

    public FuelTransactionDialog(Stage owner, FuelTransaction transaction, List<Driver> drivers) {
        this.fuelTransaction = transaction != null ? transaction : new FuelTransaction();
        setTitle(transaction != null ? "Edit Fuel Transaction" : "Add Fuel Transaction");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);

        datePicker = new DatePicker(this.fuelTransaction.getTranDate() != null ? this.fuelTransaction.getTranDate() : LocalDate.now());
        vendorField = new TextField(this.fuelTransaction.getVendor() != null ? this.fuelTransaction.getVendor() : "");
        gallonsField = new TextField(this.fuelTransaction.getQty() != 0 ? String.valueOf(this.fuelTransaction.getQty()) : "");
        costField = new TextField(this.fuelTransaction.getAmt() != 0 ? String.valueOf(this.fuelTransaction.getAmt()) : "");
        driverComboBox = new ComboBox<>();
        driverComboBox.getItems().addAll(drivers);
        driverComboBox.setValue(this.fuelTransaction.getDriver());

        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");

        saveButton.setDefaultButton(true);
        cancelButton.setCancelButton(true);

        saveButton.setOnAction(e -> {
            if (validate()) {
                updateFuelTransactionFromFields();
                saved = true;
                close();
            }
        });

        cancelButton.setOnAction(e -> close());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);

        grid.add(new Label("Vendor:"), 0, 1);
        grid.add(vendorField, 1, 1);

        grid.add(new Label("Gallons:"), 0, 2);
        grid.add(gallonsField, 1, 2);

        grid.add(new Label("Total Cost:"), 0, 3);
        grid.add(costField, 1, 3);

        grid.add(new Label("Driver:"), 0, 4);
        grid.add(driverComboBox, 1, 4);

        grid.add(saveButton, 0, 5);
        grid.add(cancelButton, 1, 5);

        setScene(new Scene(grid));
    }

    private boolean validate() {
        String errorMsg = "";
        if (datePicker.getValue() == null)
            errorMsg += "Date is required.\n";
        if (vendorField.getText().trim().isEmpty())
            errorMsg += "Vendor is required.\n";
        if (gallonsField.getText().trim().isEmpty())
            errorMsg += "Gallons is required.\n";
        else {
            try {
                Double.parseDouble(gallonsField.getText().trim());
            } catch (NumberFormatException ex) {
                errorMsg += "Gallons must be a valid number.\n";
            }
        }
        if (costField.getText().trim().isEmpty())
            errorMsg += "Total cost is required.\n";
        else {
            try {
                Double.parseDouble(costField.getText().trim());
            } catch (NumberFormatException ex) {
                errorMsg += "Total cost must be a valid number.\n";
            }
        }
        if (driverComboBox.getValue() == null)
            errorMsg += "Driver is required.\n";

        if (!errorMsg.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, errorMsg, ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void updateFuelTransactionFromFields() {
        fuelTransaction.setTranDate(datePicker.getValue());
        fuelTransaction.setVendor(vendorField.getText().trim());
        fuelTransaction.setQty(Double.parseDouble(gallonsField.getText().trim()));
        fuelTransaction.setAmt(Double.parseDouble(costField.getText().trim()));
        fuelTransaction.setDriver(driverComboBox.getValue());
    }

    public FuelTransaction getFuelTransaction() {
        return fuelTransaction;
    }

    public boolean isSaved() {
        return saved;
    }
}