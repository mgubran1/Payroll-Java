package com.company.payroll.ui;

import com.company.payroll.model.Load;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class LoadDialog extends Stage {
    private TextField loadNumberField;
    private TextField customerField;
    private TextField pickupLocationField;
    private TextField dropLocationField;
    private TextField driverIdField;
    private DatePicker deliveredDatePicker;
    private TextField grossAmountField;
    private TextField driverPercentField;
    private TextArea descriptionArea;
    private CheckBox paidCheckBox;
    private Button saveButton;
    private Button cancelButton;

    private Load load;
    private boolean saved = false;

    public LoadDialog(Stage owner, Load load) {
        this.load = (load != null) ? load : new Load();
        setTitle(load != null ? "Edit Load" : "Add Load");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);

        loadNumberField = new TextField(this.load.getLoadNumber());
        customerField = new TextField(this.load.getCustomer());
        pickupLocationField = new TextField(this.load.getPickupLocation());
        dropLocationField = new TextField(this.load.getDropLocation());
        driverIdField = new TextField(this.load.getDriverId() > 0 ? String.valueOf(this.load.getDriverId()) : "");
        deliveredDatePicker = new DatePicker(this.load.getDeliveredDate());
        grossAmountField = new TextField(this.load.getGrossAmount() != 0 ? String.valueOf(this.load.getGrossAmount()) : "");
        driverPercentField = new TextField(this.load.getDriverPercent() != 0 ? String.valueOf(this.load.getDriverPercent()) : "");
        descriptionArea = new TextArea(this.load.getDescription());
        paidCheckBox = new CheckBox("Paid");
        paidCheckBox.setSelected(this.load.isPaid());

        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");
        saveButton.setDefaultButton(true);
        cancelButton.setCancelButton(true);

        saveButton.setOnAction(e -> {
            if (validate()) {
                updateLoadFromFields();
                saved = true;
                close();
            }
        });
        cancelButton.setOnAction(e -> close());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Load Number:"), 0, 0);
        grid.add(loadNumberField, 1, 0);
        grid.add(new Label("Customer:"), 0, 1);
        grid.add(customerField, 1, 1);
        grid.add(new Label("Pickup Location:"), 0, 2);
        grid.add(pickupLocationField, 1, 2);
        grid.add(new Label("Drop Location:"), 0, 3);
        grid.add(dropLocationField, 1, 3);
        grid.add(new Label("Driver ID:"), 0, 4);
        grid.add(driverIdField, 1, 4);
        grid.add(new Label("Delivered Date:"), 0, 5);
        grid.add(deliveredDatePicker, 1, 5);
        grid.add(new Label("Gross Amount:"), 0, 6);
        grid.add(grossAmountField, 1, 6);
        grid.add(new Label("Driver Percent:"), 0, 7);
        grid.add(driverPercentField, 1, 7);
        grid.add(new Label("Description:"), 0, 8);
        grid.add(descriptionArea, 1, 8);
        grid.add(paidCheckBox, 1, 9);
        grid.add(saveButton, 0, 10);
        grid.add(cancelButton, 1, 10);

        setScene(new Scene(grid));
    }

    private boolean validate() {
        String errorMsg = "";
        if (loadNumberField.getText().trim().isEmpty()) errorMsg += "Load number required.\n";
        if (customerField.getText().trim().isEmpty()) errorMsg += "Customer required.\n";
        if (pickupLocationField.getText().trim().isEmpty()) errorMsg += "Pickup location required.\n";
        if (dropLocationField.getText().trim().isEmpty()) errorMsg += "Drop location required.\n";
        if (driverIdField.getText().trim().isEmpty()) errorMsg += "Driver ID required.\n";
        else try { Integer.parseInt(driverIdField.getText().trim()); }
        catch (NumberFormatException ex) { errorMsg += "Driver ID must be an integer.\n"; }
        if (deliveredDatePicker.getValue() == null) errorMsg += "Delivered date required.\n";
        if (grossAmountField.getText().trim().isEmpty()) errorMsg += "Gross amount required.\n";
        else try { Double.parseDouble(grossAmountField.getText().trim()); }
        catch (NumberFormatException ex) { errorMsg += "Gross amount must be a number.\n"; }
        if (driverPercentField.getText().trim().isEmpty()) errorMsg += "Driver percent required.\n";
        else try { Double.parseDouble(driverPercentField.getText().trim()); }
        catch (NumberFormatException ex) { errorMsg += "Driver percent must be a number.\n"; }
        if (!errorMsg.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, errorMsg, ButtonType.OK).showAndWait();
            return false;
        }
        return true;
    }

    private void updateLoadFromFields() {
        load.setLoadNumber(loadNumberField.getText().trim());
        load.setCustomer(customerField.getText().trim());
        load.setPickupLocation(pickupLocationField.getText().trim());
        load.setDropLocation(dropLocationField.getText().trim());
        load.setDriverId(Integer.parseInt(driverIdField.getText().trim()));
        load.setDeliveredDate(deliveredDatePicker.getValue());
        load.setGrossAmount(Double.parseDouble(grossAmountField.getText().trim()));
        load.setDriverPercent(Double.parseDouble(driverPercentField.getText().trim()));
        load.setDescription(descriptionArea.getText().trim());
        load.setPaid(paidCheckBox.isSelected());
    }

    public Load getLoad() {
        return load;
    }

    public boolean isSaved() {
        return saved;
    }
}