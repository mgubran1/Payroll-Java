package com.company.payroll.ui;

import com.company.payroll.model.CashAdvance;
import com.company.payroll.model.Driver;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class CashAdvanceDialog extends Stage {

    private ComboBox<Driver> driverComboBox;
    private DatePicker advanceDatePicker;
    private TextField amountField;
    private TextField notesField;
    private Button saveButton;
    private Button cancelButton;
    private CashAdvance cashAdvance;
    private boolean saved = false;

    public CashAdvanceDialog(Stage owner, CashAdvance cashAdvance, List<Driver> drivers) {
        this.cashAdvance = cashAdvance != null ? cashAdvance : new CashAdvance();
        setTitle(cashAdvance != null ? "Edit Cash Advance" : "Add Cash Advance");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);

        driverComboBox = new ComboBox<>();
        driverComboBox.getItems().addAll(drivers);
        driverComboBox.setValue(this.cashAdvance.getDriver());

        advanceDatePicker = new DatePicker(this.cashAdvance.getAdvanceDate() != null ? this.cashAdvance.getAdvanceDate() : LocalDate.now());
        amountField = new TextField(this.cashAdvance.getAmount() != 0 ? String.valueOf(this.cashAdvance.getAmount()) : "");
        notesField = new TextField(this.cashAdvance.getNotes() != null ? this.cashAdvance.getNotes() : "");

        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");

        saveButton.setDefaultButton(true);
        cancelButton.setCancelButton(true);

        saveButton.setOnAction(e -> {
            if (validate()) {
                updateCashAdvanceFromFields();
                saved = true;
                close();
            }
        });

        cancelButton.setOnAction(e -> close());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Driver:"), 0, 0);
        grid.add(driverComboBox, 1, 0);

        grid.add(new Label("Advance Date:"), 0, 1);
        grid.add(advanceDatePicker, 1, 1);

        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountField, 1, 2);

        grid.add(new Label("Notes:"), 0, 3);
        grid.add(notesField, 1, 3);

        grid.add(saveButton, 0, 4);
        grid.add(cancelButton, 1, 4);

        setScene(new Scene(grid));
    }

    private boolean validate() {
        String errorMsg = "";
        if (driverComboBox.getValue() == null)
            errorMsg += "Driver is required.\n";
        if (advanceDatePicker.getValue() == null)
            errorMsg += "Advance date is required.\n";
        if (amountField.getText().trim().isEmpty())
            errorMsg += "Amount is required.\n";
        else {
            try {
                Double.parseDouble(amountField.getText().trim());
            } catch (NumberFormatException ex) {
                errorMsg += "Amount must be a valid number.\n";
            }
        }

        if (!errorMsg.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, errorMsg, ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void updateCashAdvanceFromFields() {
        cashAdvance.setDriver(driverComboBox.getValue());
        cashAdvance.setAdvanceDate(advanceDatePicker.getValue());
        cashAdvance.setAmount(Double.parseDouble(amountField.getText().trim()));
        cashAdvance.setNotes(notesField.getText().trim());
    }

    public CashAdvance getCashAdvance() {
        return cashAdvance;
    }

    public boolean isSaved() {
        return saved;
    }
}