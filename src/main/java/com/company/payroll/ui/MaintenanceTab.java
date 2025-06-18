package com.company.payroll.ui;

import javafx.scene.Scene;
import com.company.payroll.dao.MaintenanceRecordDao;
import com.company.payroll.model.MaintenanceRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MaintenanceTab extends BorderPane {
    private TableView<MaintenanceRecord> table;
    private ObservableList<MaintenanceRecord> data;
    private MaintenanceRecordDao maintDao = new MaintenanceRecordDao();

    public MaintenanceTab() {
        data = FXCollections.observableArrayList(maintDao.getAllMaintenanceRecords());
        table = new TableView<>(data);

        TableColumn<MaintenanceRecord, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<MaintenanceRecord, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<MaintenanceRecord, String> truckCol = new TableColumn<>("Truck");
        truckCol.setCellValueFactory(new PropertyValueFactory<>("truckId"));

        TableColumn<MaintenanceRecord, String> trailerCol = new TableColumn<>("Trailer");
        trailerCol.setCellValueFactory(new PropertyValueFactory<>("trailerNumber"));

        TableColumn<MaintenanceRecord, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<MaintenanceRecord, BigDecimal> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<MaintenanceRecord, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        table.getColumns().addAll(idCol, dateCol, truckCol, trailerCol, catCol, amountCol, descCol);

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> showMaintDialog(null));

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> {
            MaintenanceRecord m = table.getSelectionModel().getSelectedItem();
            if (m != null) showMaintDialog(m);
        });

        Button delBtn = new Button("Delete");
        delBtn.setOnAction(e -> {
            MaintenanceRecord m = table.getSelectionModel().getSelectedItem();
            if (m != null) {
                maintDao.deleteMaintenanceRecord(m.getId());
                refresh();
            }
        });

        HBox controls = new HBox(10, addBtn, editBtn, delBtn);
        controls.setPadding(new Insets(10));

        setCenter(table);
        setBottom(controls);
    }

    private void showMaintDialog(MaintenanceRecord m) {
        Stage dialog = new Stage();
        dialog.setTitle(m == null ? "Add Maintenance Record" : "Edit Maintenance Record");

        DatePicker datePicker = new DatePicker(m == null ? LocalDate.now() : m.getDate());
        TextField truckField = new TextField(m == null ? "" : m.getTruckId());
        TextField trailerField = new TextField(m == null ? "" : m.getTrailerNumber());
        TextField catField = new TextField(m == null ? "" : m.getCategory());
        TextField amountField = new TextField(m == null ? "" : String.valueOf(m.getAmount() == null ? "" : m.getAmount()));
        TextField descField = new TextField(m == null ? "" : m.getDescription());

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                if (m == null) {
                    MaintenanceRecord mr = new MaintenanceRecord();
                    mr.setDate(datePicker.getValue());
                    mr.setTruckId(truckField.getText());
                    mr.setTrailerNumber(trailerField.getText());
                    mr.setCategory(catField.getText());
                    mr.setAmount(new BigDecimal(amountField.getText()));
                    mr.setDescription(descField.getText());
                    maintDao.addMaintenanceRecord(mr);
                } else {
                    m.setDate(datePicker.getValue());
                    m.setTruckId(truckField.getText());
                    m.setTrailerNumber(trailerField.getText());
                    m.setCategory(catField.getText());
                    m.setAmount(new BigDecimal(amountField.getText()));
                    m.setDescription(descField.getText());
                    maintDao.updateMaintenanceRecord(m);
                }
                refresh();
                dialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox vbox = new VBox(10,
            new Label("Date"), datePicker,
            new Label("Truck"), truckField,
            new Label("Trailer"), trailerField,
            new Label("Category"), catField,
            new Label("Amount"), amountField,
            new Label("Description"), descField,
            saveBtn
        );
        vbox.setPadding(new Insets(20));
        dialog.setScene(new Scene(vbox));
        dialog.show();
    }

    private void refresh() {
        data.setAll(maintDao.getAllMaintenanceRecords());
    }
}