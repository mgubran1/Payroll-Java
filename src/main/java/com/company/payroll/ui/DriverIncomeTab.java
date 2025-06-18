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

import java.math.BigDecimal;
import java.time.LocalDate;

public class DriverIncomeTab extends BorderPane {
    private TableView<Payroll> table;
    private ObservableList<Payroll> data;
    private PayrollDao payrollDao = new PayrollDao();

    public DriverIncomeTab() {
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

        TableColumn<Payroll, BigDecimal> netCol = new TableColumn<>("Net");
        netCol.setCellValueFactory(new PropertyValueFactory<>("net"));

        table.getColumns().addAll(idCol, driverIdCol, startCol, endCol, grossCol, netCol);

        setCenter(table);
        setPadding(new Insets(10));
    }
}