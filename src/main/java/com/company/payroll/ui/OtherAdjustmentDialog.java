package com.company.payroll.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OtherAdjustmentDialog extends Stage {
    // Add fields relevant to adjustment (not deduction)
    private TextField adjustmentReasonField;
    private TextField adjustmentAmountField;
    private Button saveButton;
    private Button cancelButton;
    private boolean saved = false;

    public OtherAdjustmentDialog(Stage owner) {
        setTitle("Other Adjustment");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);

        adjustmentReasonField = new TextField();
        adjustmentAmountField = new TextField();

        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");
        saveButton.setDefaultButton(true);
        cancelButton.setCancelButton(true);

        saveButton.setOnAction(e -> {
            // Optionally add validation here
            saved = true;
            close();
        });
        cancelButton.setOnAction(e -> close());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Adjustment Reason:"), 0, 0);
        grid.add(adjustmentReasonField, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(adjustmentAmountField, 1, 1);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(saveButton, cancelButton);
        grid.add(buttonBar, 0, 2, 2, 1);

        setScene(new Scene(grid));
    }

    public String getAdjustmentReason() {
        return adjustmentReasonField.getText().trim();
    }

    public String getAdjustmentAmount() {
        return adjustmentAmountField.getText().trim();
    }

    public boolean isSaved() {
        return saved;
    }
}