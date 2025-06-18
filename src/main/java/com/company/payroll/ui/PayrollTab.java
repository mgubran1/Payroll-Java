package com.company.payroll.ui;

import javafx.scene.Scene;
import com.company.payroll.dao.PayrollDao;
import com.company.payroll.model.Payroll;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PayrollTab extends BorderPane {
    private TableView<Payroll> table;
    private ObservableList<Payroll> data;
    private PayrollDao payrollDao = new PayrollDao();

    public PayrollTab() {
        data = FXCollections.observableArrayList(payrollDao.getAllPayrolls());
        table = new TableView<>(data);

        TableColumn<Payroll, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Payroll, Integer> driverIdCol = new TableColumn<>("Driver ID");
        driverIdCol.setCellValueFactory(new PropertyValueFactory<>("driverId"));

        TableColumn<Payroll, LocalDate> startCol = new TableColumn<>("Week Start");
        startCol.setCellValueFactory(new PropertyValueFactory<>("weekStart"));

        TableColumn<Payroll, LocalDate> endCol = new TableColumn<>("Week End");
        endCol.setCellValueFactory(new PropertyValueFactory<>("weekEnd"));

        TableColumn<Payroll, BigDecimal> grossCol = new TableColumn<>("Gross");
        grossCol.setCellValueFactory(new PropertyValueFactory<>("gross"));

        TableColumn<Payroll, BigDecimal> deductionsCol = new TableColumn<>("Deductions");
        deductionsCol.setCellValueFactory(new PropertyValueFactory<>("deductions"));

        TableColumn<Payroll, BigDecimal> netCol = new TableColumn<>("Net");
        netCol.setCellValueFactory(new PropertyValueFactory<>("net"));

        table.getColumns().addAll(idCol, driverIdCol, startCol, endCol, grossCol, deductionsCol, netCol);

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> showPayrollDialog(null));

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> {
            Payroll p = table.getSelectionModel().getSelectedItem();
            if (p != null) showPayrollDialog(p);
        });

        Button delBtn = new Button("Delete");
        delBtn.setOnAction(e -> {
            Payroll p = table.getSelectionModel().getSelectedItem();
            if (p != null) {
                payrollDao.deletePayroll(p.getId());
                refresh();
            }
        });

        HBox controls = new HBox(10, addBtn, editBtn, delBtn);
        controls.setPadding(new Insets(10));

        setCenter(table);
        setBottom(controls);
    }

    private void showPayrollDialog(Payroll p) {
        Stage dialog = new Stage();
        dialog.setTitle(p == null ? "Add Payroll" : "Edit Payroll");

        TextField driverIdField = new TextField(p == null ? "" : String.valueOf(p.getDriverId()));
        DatePicker startPicker = new DatePicker(p == null ? LocalDate.now() : p.getWeekStart());
        DatePicker endPicker = new DatePicker(p == null ? LocalDate.now() : p.getWeekEnd());
        TextField grossField = new TextField(p == null ? "" : String.valueOf(p.getGross()));
        TextField deductionsField = new TextField(p == null ? "" : String.valueOf(p.getDeductions()));
        TextField netField = new TextField(p == null ? "" : String.valueOf(p.getNet()));

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                if (p == null) {
                    Payroll pr = new Payroll();
                    pr.setDriverId(Integer.parseInt(driverIdField.getText()));
                    pr.setWeekStart(startPicker.getValue());
                    pr.setWeekEnd(endPicker.getValue());
                    pr.setGross(new BigDecimal(grossField.getText()));
                    pr.setDeductions(new BigDecimal(deductionsField.getText()));
                    pr.setNet(new BigDecimal(netField.getText()));
                    payrollDao.addPayroll(pr);
                } else {
                    p.setDriverId(Integer.parseInt(driverIdField.getText()));
                    p.setWeekStart(startPicker.getValue());
                    p.setWeekEnd(endPicker.getValue());
                    p.setGross(new BigDecimal(grossField.getText()));
                    p.setDeductions(new BigDecimal(deductionsField.getText()));
                    p.setNet(new BigDecimal(netField.getText()));
                    payrollDao.updatePayroll(p);
                }
                refresh();
                dialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox vbox = new VBox(10,
            new Label("Driver ID"), driverIdField,
            new Label("Week Start"), startPicker,
            new Label("Week End"), endPicker,
            new Label("Gross"), grossField,
            new Label("Deductions"), deductionsField,
            new Label("Net"), netField,
            saveBtn
        );
        vbox.setPadding(new Insets(20));
        dialog.setScene(new Scene(vbox));
        dialog.show();
    }

    private void refresh() {
        data.setAll(payrollDao.getAllPayrolls());
    }
}