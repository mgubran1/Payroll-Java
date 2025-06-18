package com.company.payroll.ui;

import com.company.payroll.model.MonthlyFee;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class MonthlyFeeDialog extends Stage {

    private TextField driverIdField;
    private DatePicker dueDatePicker;
    private TextField feeTypeField;
    private TextField amountField;
    private TextField weeklyFeeField;
    private TextArea notesField;
    private Button saveButton;
    private Button cancelButton;
    private MonthlyFee monthlyFee;
    private boolean saved = false;

    public MonthlyFeeDialog(Stage owner, MonthlyFee monthlyFee) {
        this.monthlyFee = monthlyFee != null ? monthlyFee : new MonthlyFee();
        setTitle(monthlyFee != null ? "Edit Monthly Fee" : "Add Monthly Fee");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);

        driverIdField = new TextField(this.monthlyFee.getDriverId() > 0 ? String.valueOf(this.monthlyFee.getDriverId()) : "");
        dueDatePicker = new DatePicker(this.monthlyFee.getDueDate() != null ? this.monthlyFee.getDueDate() : LocalDate.now());
        feeTypeField = new TextField(this.monthlyFee.getFeeType() != null ? this.monthlyFee.getFeeType() : "");
        amountField = new TextField(this.monthlyFee.getAmount() != 0 ? String.valueOf(this.monthlyFee.getAmount()) : "");
        weeklyFeeField = new TextField(this.monthlyFee.getWeeklyFee() != 0 ? String.valueOf(this.monthlyFee.getWeeklyFee()) : "");
        notesField = new TextArea(this.monthlyFee.getNotes() != null ? this.monthlyFee.getNotes() : "");

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

        grid.add(new Label("Driver ID:"), 0, 0);
        grid.add(driverIdField, 1, 0);
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
        if (driverIdField.getText().trim().isEmpty()) errorMsg += "Driver ID required.\n";
        else try { Integer.parseInt(driverIdField.getText().trim()); }
        catch (NumberFormatException ex) { errorMsg += "Driver ID must be an integer.\n"; }
        if (dueDatePicker.getValue() == null) errorMsg += "Due Date required.\n";
        if (feeTypeField.getText().trim().isEmpty()) errorMsg += "Fee Type required.\n";
        if (amountField.getText().trim().isEmpty()) errorMsg += "Amount required.\n";
        else try { Double.parseDouble(amountField.getText().trim()); }
        catch (NumberFormatException ex) { errorMsg += "Amount must be a number.\n"; }
        if (!weeklyFeeField.getText().trim().isEmpty()) {
            try { Double.parseDouble(weeklyFeeField.getText().trim()); }
            catch (NumberFormatException ex) { errorMsg += "Weekly Fee must be a number.\n"; }
        }
        if (!errorMsg.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, errorMsg, ButtonType.OK).showAndWait();
            return false;
        }
        return true;
    }

    private void updateMonthlyFeeFromFields() {
        monthlyFee.setDriverId(Integer.parseInt(driverIdField.getText().trim()));
        monthlyFee.setDueDate(dueDatePicker.getValue());
        monthlyFee.setFeeType(feeTypeField.getText().trim());
        monthlyFee.setAmount(Double.parseDouble(amountField.getText().trim()));
        if (!weeklyFeeField.getText().trim().isEmpty()) {
            monthlyFee.setWeeklyFee(Double.parseDouble(weeklyFeeField.getText().trim()));
        } else {
            monthlyFee.setWeeklyFee(0);
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