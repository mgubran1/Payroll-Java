package com.company.payroll.ui;

import com.company.payroll.model.Driver;
import com.company.payroll.model.MonthlyFee;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class MonthlyFeeDialog extends Stage {

    private ComboBox<Driver> driverComboBox;
    private DatePicker dueDatePicker;
    private TextField feeTypeField;
    private TextField amountField;
    private TextField weeklyFeeField;
    private TextField notesField;
    private Button saveButton;
    private Button cancelButton;
    private MonthlyFee monthlyFee;
    private boolean saved = false;

    public MonthlyFeeDialog(Stage owner, MonthlyFee monthlyFee, List<Driver> drivers) {
        this.monthlyFee = monthlyFee != null ? monthlyFee : new MonthlyFee();
        setTitle(monthlyFee != null ? "Edit Monthly Fee" : "Add Monthly Fee");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);

        driverComboBox = new ComboBox<>();
        driverComboBox.getItems().addAll(drivers);
        driverComboBox.setValue(this.monthlyFee.getDriver());

        dueDatePicker = new DatePicker(this.monthlyFee.getDueDate() != null ? this.monthlyFee.getDueDate() : LocalDate.now());
        feeTypeField = new TextField(this.monthlyFee.getFeeType() != null ? this.monthlyFee.getFeeType() : "");
        amountField = new TextField(this.monthlyFee.getAmount() != 0 ? String.valueOf(this.monthlyFee.getAmount()) : "");
        weeklyFeeField = new TextField(this.monthlyFee.getWeeklyFee() != 0 ? String.valueOf(this.monthlyFee.getWeeklyFee()) : "");
        notesField = new TextField(this.monthlyFee.getNotes() != null ? this.monthlyFee.getNotes() : "");

        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");

        saveButton.setDefaultButton(true);
        cancelButton.setCancelButton(true);

        saveButton.setOnAction(e -> {
            if (validate()) {
                updateMonthlyFeeFromFields();
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

        grid.add(new Label("Due Date:"), 0, 1);
        grid.add(dueDatePicker, 1, 1);

        grid.add(new Label("Fee Type:"), 0, 2);
        grid.add(feeTypeField, 1, 2);

        grid.add(new Label("Amount:"), 0, 3);
        grid.add(amountField, 1, 3);

        grid.add(new Label("Weekly Fee:"), 0, 4);
        grid.add(weeklyFeeField, 1, 4);

        grid.add(new Label("Notes:"), 0, 5);
        grid.add(notesField, 1, 5);

        grid.add(saveButton, 0, 6);
        grid.add(cancelButton, 1, 6);

        setScene(new Scene(grid));
    }

    private boolean validate() {
        String errorMsg = "";
        if (driverComboBox.getValue() == null)
            errorMsg += "Driver is required.\n";
        if (dueDatePicker.getValue() == null)
            errorMsg += "Due date is required.\n";
        if (feeTypeField.getText().trim().isEmpty())
            errorMsg += "Fee type is required.\n";
        if (amountField.getText().trim().isEmpty())
            errorMsg += "Amount is required.\n";
        else {
            try {
                Double.parseDouble(amountField.getText().trim());
            } catch (NumberFormatException ex) {
                errorMsg += "Amount must be a valid number.\n";
            }
        }
        if (!weeklyFeeField.getText().trim().isEmpty()) {
            try {
                Double.parseDouble(weeklyFeeField.getText().trim());
            } catch (NumberFormatException ex) {
                errorMsg += "Weekly fee must be a valid number.\n";
            }
        }

        if (!errorMsg.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, errorMsg, ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void updateMonthlyFeeFromFields() {
        monthlyFee.setDriver(driverComboBox.getValue());
        monthlyFee.setDueDate(dueDatePicker.getValue());
        monthlyFee.setFeeType(feeTypeField.getText().trim());
        monthlyFee.setAmount(Double.parseDouble(amountField.getText().trim()));
        if (!weeklyFeeField.getText().trim().isEmpty()) {
            monthlyFee.setWeeklyFee(Double.parseDouble(weeklyFeeField.getText().trim()));
        }
        monthlyFee.setNotes(notesField.getText().trim());
    }

    public MonthlyFee getMonthlyFee() {
        return monthlyFee;
    }

    public boolean isSaved() {
        return saved;
    }
}