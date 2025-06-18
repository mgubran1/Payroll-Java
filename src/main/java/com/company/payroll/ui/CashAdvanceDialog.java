package com.company.payroll.ui;

import com.company.payroll.model.CashAdvance;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CashAdvanceDialog extends Dialog<CashAdvance> {
    private final Spinner<Double> amountSpinner = new Spinner<>(0, 10000, 0, 0.01);
    private final DatePicker issuedDatePicker = new DatePicker(LocalDate.now());
    private final Spinner<Double> repaymentSpinner = new Spinner<>(0, 10000, 0, 0.01);
    private final Spinner<Double> weeklySpinner = new Spinner<>(0, 10000, 0, 0.01);
    private final TextField notesField = new TextField();

    public CashAdvanceDialog(CashAdvance advance) {
        setTitle((advance == null ? "Add" : "Edit") + " Cash Advance");

        if (advance != null) {
            amountSpinner.getValueFactory().setValue(advance.getAmount() != null ? advance.getAmount().doubleValue() : 0.0);
            issuedDatePicker.setValue(advance.getIssuedDate() != null ? advance.getIssuedDate() : LocalDate.now());
            repaymentSpinner.getValueFactory().setValue(advance.getWeeklyRepayment() != null ? advance.getWeeklyRepayment().doubleValue() : 0.0);
            weeklySpinner.getValueFactory().setValue(advance.getWeeklyRepayment() != null ? advance.getWeeklyRepayment().doubleValue() : 0.0);
            notesField.setText(advance.getNotes() != null ? advance.getNotes() : "");
        }

        GridPane grid = new GridPane();
        grid.setHgap(5); grid.setVgap(8); grid.setPadding(new Insets(12));
        grid.addRow(0, new Label("Total Advance:"), amountSpinner);
        grid.addRow(1, new Label("Issued Date:"), issuedDatePicker);
        grid.addRow(2, new Label("Repayment This Period:"), repaymentSpinner);
        grid.addRow(3, new Label("Default Weekly Repayment:"), weeklySpinner);
        grid.addRow(4, new Label("Notes:"), notesField);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                CashAdvance ca = (advance == null ? new CashAdvance() : advance);
                ca.setAmount(BigDecimal.valueOf(amountSpinner.getValue()));
                ca.setIssuedDate(issuedDatePicker.getValue());
                ca.setWeeklyRepayment(BigDecimal.valueOf(weeklySpinner.getValue()));
                ca.setNotes(notesField.getText());
                return ca;
            }
            return null;
        });
    }
}