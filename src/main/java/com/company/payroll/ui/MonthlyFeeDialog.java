package com.company.payroll.ui;

import com.company.payroll.model.FeeType;
import com.company.payroll.model.MonthlyFee;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MonthlyFeeDialog extends Dialog<MonthlyFee> {

    private final ComboBox<FeeType> typeCombo = new ComboBox<>();
    private final Spinner<Double> amountSpinner = new Spinner<>(0, 10000, 0, 0.01);
    private final DatePicker dueDatePicker = new DatePicker(LocalDate.now());
    private final Spinner<Double> weeklySpinner = new Spinner<>(0, 10000, 0, 0.01);
    private final TextField notesField = new TextField();

    public MonthlyFeeDialog(MonthlyFee fee) {
        setTitle((fee == null ? "Add" : "Edit") + " Monthly Fee");
        typeCombo.getItems().setAll(FeeType.values());
        typeCombo.setEditable(false);

        if (fee != null) {
            typeCombo.setValue(FeeType.valueOf(fee.getFeeType().toUpperCase()));
            amountSpinner.getValueFactory().setValue(fee.getAmount() != null ? fee.getAmount().doubleValue() : 0.0);
            dueDatePicker.setValue(fee.getDueDate() != null ? fee.getDueDate() : LocalDate.now());
            weeklySpinner.getValueFactory().setValue(fee.getWeeklyFee() != null ? fee.getWeeklyFee().doubleValue() : 0.0);
            notesField.setText(fee.getNotes() != null ? fee.getNotes() : "");
        }

        GridPane grid = new GridPane();
        grid.setHgap(5); grid.setVgap(8); grid.setPadding(new Insets(12));
        grid.addRow(0, new Label("Fee Type:"), typeCombo);
        grid.addRow(1, new Label("Total Amount:"), amountSpinner);
        grid.addRow(2, new Label("Due Date:"), dueDatePicker);
        grid.addRow(3, new Label("Weekly Fee:"), weeklySpinner);
        grid.addRow(4, new Label("Notes:"), notesField);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                if (typeCombo.getValue() == null) return null;
                MonthlyFee mf = (fee == null ? new MonthlyFee() : fee);
                mf.setFeeType(typeCombo.getValue().name());
                mf.setAmount(BigDecimal.valueOf(amountSpinner.getValue()));
                mf.setDueDate(dueDatePicker.getValue());
                mf.setWeeklyFee(BigDecimal.valueOf(weeklySpinner.getValue()));
                mf.setNotes(notesField.getText());
                return mf;
            }
            return null;
        });
    }
}