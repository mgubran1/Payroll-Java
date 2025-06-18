package com.company.payroll.ui;

import com.company.payroll.model.FuelTransaction;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class FuelTransactionDialog extends Stage {
    private TextField driverIdField;
    private DatePicker datePicker;
    private TextField vendorField;
    private TextField gallonsField;
    private TextField costField;
    private TextArea notesField;
    private Button saveButton;
    private Button cancelButton;
    private FuelTransaction fuelTransaction;
    private boolean saved = false;

    public FuelTransactionDialog(Stage owner, FuelTransaction transaction) {
        this.fuelTransaction = transaction != null ? transaction : new FuelTransaction();
        setTitle(transaction != null ? "Edit Fuel Transaction" : "Add Fuel Transaction");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);

        driverIdField = new TextField(this.fuelTransaction.getDriverId() != null && this.fuelTransaction.getDriverId() > 0 ? String.valueOf(this.fuelTransaction.getDriverId()) : "");
        // Model stores tranDate as String (yyyy-MM-dd), so parse it
        LocalDate initialDate = null;
        try {
            if (this.fuelTransaction.getTranDate() != null) {
                initialDate = LocalDate.parse(this.fuelTransaction.getTranDate());
            }
        } catch (Exception ignored) {}
        datePicker = new DatePicker(initialDate != null ? initialDate : LocalDate.now());
        vendorField = new TextField(this.fuelTransaction.getVendor() != null ? this.fuelTransaction.getVendor() : "");
        gallonsField = new TextField(this.fuelTransaction.getQty() != null && !this.fuelTransaction.getQty().isBlank() ? this.fuelTransaction.getQty() : "");
        costField = new TextField(this.fuelTransaction.getAmt() != null && !this.fuelTransaction.getAmt().isBlank() ? this.fuelTransaction.getAmt() : "");
        notesField = new TextArea(this.fuelTransaction.getNotes() != null ? this.fuelTransaction.getNotes() : "");

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

        grid.add(new Label("Driver ID:"), 0, 0);
        grid.add(driverIdField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Vendor:"), 0, 2);
        grid.add(vendorField, 1, 2);
        grid.add(new Label("Gallons:"), 0, 3);
        grid.add(gallonsField, 1, 3);
        grid.add(new Label("Total Cost:"), 0, 4);
        grid.add(costField, 1, 4);
        grid.add(new Label("Notes:"), 0, 5);
        grid.add(notesField, 1, 5);
        grid.add(saveButton, 0, 6);
        grid.add(cancelButton, 1, 6);

        setScene(new Scene(grid));
    }

    private boolean validate() {
        String errorMsg = "";
        if (driverIdField.getText().trim().isEmpty()) errorMsg += "Driver ID required.\n";
        else try { Integer.parseInt(driverIdField.getText().trim()); }
        catch (NumberFormatException ex) { errorMsg += "Driver ID must be an integer.\n"; }
        if (datePicker.getValue() == null) errorMsg += "Date required.\n";
        if (vendorField.getText().trim().isEmpty()) errorMsg += "Vendor required.\n";
        if (gallonsField.getText().trim().isEmpty()) errorMsg += "Gallons required.\n";
        else try { Double.parseDouble(gallonsField.getText().trim()); }
        catch (NumberFormatException ex) { errorMsg += "Gallons must be a number.\n"; }
        if (costField.getText().trim().isEmpty()) errorMsg += "Total cost required.\n";
        else try { Double.parseDouble(costField.getText().trim()); }
        catch (NumberFormatException ex) { errorMsg += "Total cost must be a number.\n"; }
        if (!errorMsg.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, errorMsg, ButtonType.OK).showAndWait();
            return false;
        }
        return true;
    }

    private void updateFuelTransactionFromFields() {
        fuelTransaction.setDriverId(Integer.parseInt(driverIdField.getText().trim()));
        // Store as yyyy-MM-dd String
        fuelTransaction.setTranDate(datePicker.getValue().toString());
        fuelTransaction.setVendor(vendorField.getText().trim());
        fuelTransaction.setQty(gallonsField.getText().trim());
        fuelTransaction.setAmt(costField.getText().trim());
        fuelTransaction.setNotes(notesField.getText().trim());
    }

    public FuelTransaction getFuelTransaction() {
        return fuelTransaction;
    }

    public boolean isSaved() {
        return saved;
    }
}