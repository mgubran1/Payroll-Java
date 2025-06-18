package com.company.payroll.ui;

import com.company.payroll.model.AdjustmentType;
import com.company.payroll.model.OtherDeduction;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OtherAdjustmentDialog extends Dialog<OtherDeduction> {

    private final DatePicker datePicker = new DatePicker(LocalDate.now());
    private final Spinner<Double> amountSpinner = new Spinner<>(0, 10000, 0, 0.01);
    private final TextField reasonField = new TextField();
    private final ComboBox<AdjustmentType> typeCombo = new ComboBox<>();

    public OtherAdjustmentDialog(OtherDeduction adjustment) {
        setTitle((adjustment == null ? "Add" : "Edit") + " Other Adjustment");
        typeCombo.getItems().setAll(AdjustmentType.values());
        typeCombo.setEditable(false);

        if (adjustment != null) {
            datePicker.setValue(adjustment.getDate() != null ? adjustment.getDate() : LocalDate.now());
            amountSpinner.getValueFactory().setValue(adjustment.getAmount() != null ? adjustment.getAmount().doubleValue() : 0.0);
            reasonField.setText(adjustment.getReason() != null ? adjustment.getReason() : "");
            typeCombo.setValue(adjustment.getReimbursed() == 1 ? AdjustmentType.Reimbursement : AdjustmentType.Deduction);
        }

        GridPane grid = new GridPane();
        grid.setHgap(5); grid.setVgap(8); grid.setPadding(new Insets(12));
        grid.addRow(0, new Label("Date:"), datePicker);
        grid.addRow(1, new Label("Amount:"), amountSpinner);
        grid.addRow(2, new Label("Reason:"), reasonField);
        grid.addRow(3, new Label("Type:"), typeCombo);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                if (typeCombo.getValue() == null) return null;
                OtherDeduction d = (adjustment == null ? new OtherDeduction() : adjustment);
                d.setDate(datePicker.getValue());
                d.setAmount(BigDecimal.valueOf(amountSpinner.getValue()));
                d.setReason(reasonField.getText());
                d.setReimbursed(typeCombo.getValue() == AdjustmentType.Reimbursement ? 1 : 0);
                return d;
            }
            return null;
        });
    }
}