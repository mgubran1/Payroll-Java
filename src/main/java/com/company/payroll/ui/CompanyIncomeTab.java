package com.company.payroll.ui;

import javafx.scene.Scene;
import com.company.payroll.dao.CompanyIncomeDao;
import com.company.payroll.model.CompanyIncome;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CompanyIncomeTab extends BorderPane {
    private TableView<CompanyIncome> table;
    private ObservableList<CompanyIncome> data;
    private CompanyIncomeDao incomeDao = new CompanyIncomeDao();

    public CompanyIncomeTab() {
        data = FXCollections.observableArrayList(incomeDao.getAllCompanyIncomes());
        table = new TableView<>(data);

        TableColumn<CompanyIncome, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<CompanyIncome, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<CompanyIncome, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customer"));

        TableColumn<CompanyIncome, BigDecimal> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<CompanyIncome, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        TableColumn<CompanyIncome, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        table.getColumns().addAll(idCol, dateCol, customerCol, amountCol, notesCol, typeCol);

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> showIncomeDialog(null));

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> {
            CompanyIncome ci = table.getSelectionModel().getSelectedItem();
            if (ci != null) showIncomeDialog(ci);
        });

        Button delBtn = new Button("Delete");
        delBtn.setOnAction(e -> {
            CompanyIncome ci = table.getSelectionModel().getSelectedItem();
            if (ci != null) {
                incomeDao.deleteCompanyIncome(ci.getId());
                refresh();
            }
        });

        HBox controls = new HBox(10, addBtn, editBtn, delBtn);
        controls.setPadding(new Insets(10));

        setCenter(table);
        setBottom(controls);
    }

    private void showIncomeDialog(CompanyIncome ci) {
        Stage dialog = new Stage();
        dialog.setTitle(ci == null ? "Add Company Income" : "Edit Company Income");

        DatePicker datePicker = new DatePicker(ci == null ? LocalDate.now() : ci.getDate());
        TextField customerField = new TextField(ci == null ? "" : ci.getCustomer());
        TextField amountField = new TextField(ci == null ? "" : String.valueOf(ci.getAmount() == null ? "" : ci.getAmount()));
        TextField notesField = new TextField(ci == null ? "" : ci.getNotes());
        TextField typeField = new TextField(ci == null ? "" : ci.getType());

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                if (ci == null) {
                    CompanyIncome c = new CompanyIncome();
                    c.setDate(datePicker.getValue());
                    c.setCustomer(customerField.getText());
                    c.setAmount(new BigDecimal(amountField.getText()));
                    c.setNotes(notesField.getText());
                    c.setType(typeField.getText());
                    incomeDao.addCompanyIncome(c);
                } else {
                    ci.setDate(datePicker.getValue());
                    ci.setCustomer(customerField.getText());
                    ci.setAmount(new BigDecimal(amountField.getText()));
                    ci.setNotes(notesField.getText());
                    ci.setType(typeField.getText());
                    incomeDao.updateCompanyIncome(ci);
                }
                refresh();
                dialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox vbox = new VBox(10,
            new Label("Date"), datePicker,
            new Label("Customer"), customerField,
            new Label("Amount"), amountField,
            new Label("Notes"), notesField,
            new Label("Type"), typeField,
            saveBtn
        );
        vbox.setPadding(new Insets(20));
        dialog.setScene(new Scene(vbox));
        dialog.show();
    }

    private void refresh() {
        data.setAll(incomeDao.getAllCompanyIncomes());
    }
}