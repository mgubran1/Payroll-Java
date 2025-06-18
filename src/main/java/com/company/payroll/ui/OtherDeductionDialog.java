package com.company.payroll.ui;

import com.company.payroll.model.OtherDeduction;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class OtherDeductionDialog extends Stage {

    private TextField driverIdField;
    private DatePicker datePicker;
    private TextField deductionTypeField;
    private TextField amountField;
    private TextArea notesField;
    private Button saveButton;
    private Button cancelButton;
    private OtherDeduction otherDeduction;
    private boolean saved = false;

    public OtherDeductionDialog(Stage owner, OtherDeduction otherDeduction) {
        this.otherDeduction = otherDeduction != null ? otherDeduction : new OtherDeduction();
        setTitle(otherDeduction != null ? "Edit Other Deduction" : "Add Other Deduction");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);

        driverIdField = new TextField(this.otherDeduction.getDriverId() > 0 ? String.valueOf(this.otherDeduction.getDriverId()) : "");
        datePicker = new DatePicker(this.otherDeduction.getDate() != null ? this.otherDeduction.getDate() : LocalDate.now());
        deductionTypeField = new TextField(this.otherDeduction.getType() != null ? this.otherDeduction.getType() : "");
        amountField = new TextField(this.otherDeduction.getAmount() != 0 ? String.valueOf(this.otherDeduction.getAmount()) : "");
        notesField = new TextArea(this.otherDeduction.getNotes() != null ? this.otherDeduction.getNotes() : "");

        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");
        saveButton.setDefaultButton(true);
        cancelButton.setCancelButton(true);

        saveButton.setOnAction(e -> {
            if (validate()) {
                updateOtherDeductionFromFields();
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
        grid.add(new Label("Deduction Type:"), 0, 2);
        grid.add(deductionTypeField, 1, 2);
        grid.add(new Label("Amount:"), 0, 3);
        grid.add(amountField, 1, 3);
        grid.add(new Label("Notes:"), 0, 4);
        grid.add(notesField, 1, 4);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(saveButton, cancelButton);
        grid.add(buttonBar, 0, 5, 2, 1);

        setScene(new Scene(grid));
    }

    private boolean validate() {
        StringBuilder errorMsg = new StringBuilder();
        if (driverIdField.getText().trim().isEmpty()) errorMsg.append("Driver ID required.\n");
        else try { Integer.parseInt(driverIdField.getText().trim()); }
        catch (NumberFormatException ex) { errorMsg.append("Driver ID must be an integer.\n"); }
        if (datePicker.getValue() == null) errorMsg.append("Date required.\n");
        if (deductionTypeField.getText().trim().isEmpty()) errorMsg.append("Deduction type required.\n");
        if (amountField.getText().trim().isEmpty()) errorMsg.append("Amount required.\n");
        else try { Double.parseDouble(amountField.getText().trim()); }
        catch (NumberFormatException ex) { errorMsg.append("Amount must be a number.\n"); }
        if (errorMsg.length() > 0) {
            new Alert(Alert.AlertType.ERROR, errorMsg.toString(), ButtonType.OK).showAndWait();
            return false;
        }
        return true;
    }

    private void updateOtherDeductionFromFields() {
        otherDeduction.setDriverId(Integer.parseInt(driverIdField.getText().trim()));
        otherDeduction.setDate(datePicker.getValue());
        otherDeduction.setType(deductionTypeField.getText().trim());
        otherDeduction.setAmount(Double.parseDouble(amountField.getText().trim()));
        otherDeduction.setNotes(notesField.getText().trim());
    }

    public OtherDeduction getOtherDeduction() {
        return otherDeduction;
    }

    public boolean isSaved() {
        return saved;
    }
}