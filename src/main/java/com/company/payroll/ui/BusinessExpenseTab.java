package com.company.payroll.ui;

import javafx.scene.Scene;
import com.company.payroll.dao.BusinessExpenseDao;
import com.company.payroll.model.BusinessExpense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BusinessExpenseTab extends BorderPane {
    private TableView<BusinessExpense> table;
    private ObservableList<BusinessExpense> data;
    private BusinessExpenseDao expenseDao = new BusinessExpenseDao();

    public BusinessExpenseTab() {
        data = FXCollections.observableArrayList(expenseDao.getAllBusinessExpenses());
        table = new TableView<>(data);

        TableColumn<BusinessExpense, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<BusinessExpense, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<BusinessExpense, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<BusinessExpense, BigDecimal> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<BusinessExpense, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        table.getColumns().addAll(idCol, dateCol, categoryCol, amountCol, descCol);

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> showExpenseDialog(null));

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> {
            BusinessExpense be = table.getSelectionModel().getSelectedItem();
            if (be != null) showExpenseDialog(be);
        });

        Button delBtn = new Button("Delete");
        delBtn.setOnAction(e -> {
            BusinessExpense be = table.getSelectionModel().getSelectedItem();
            if (be != null) {
                expenseDao.deleteBusinessExpense(be.getId());
                refresh();
            }
        });

        HBox controls = new HBox(10, addBtn, editBtn, delBtn);
        controls.setPadding(new Insets(10));

        setCenter(table);
        setBottom(controls);
    }

    private void showExpenseDialog(BusinessExpense be) {
        Stage dialog = new Stage();
        dialog.setTitle(be == null ? "Add Business Expense" : "Edit Business Expense");

        DatePicker datePicker = new DatePicker(be == null ? LocalDate.now() : be.getDate());
        TextField categoryField = new TextField(be == null ? "" : be.getCategory());
        TextField amountField = new TextField(be == null ? "" : String.valueOf(be.getAmount() == null ? "" : be.getAmount()));
        TextField descField = new TextField(be == null ? "" : be.getDescription());

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                if (be == null) {
                    BusinessExpense b = new BusinessExpense();
                    b.setDate(datePicker.getValue());
                    b.setCategory(categoryField.getText());
                    b.setAmount(new BigDecimal(amountField.getText()));
                    b.setDescription(descField.getText());
                    expenseDao.addBusinessExpense(b);
                } else {
                    be.setDate(datePicker.getValue());
                    be.setCategory(categoryField.getText());
                    be.setAmount(new BigDecimal(amountField.getText()));
                    be.setDescription(descField.getText());
                    expenseDao.updateBusinessExpense(be);
                }
                refresh();
                dialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox vbox = new VBox(10,
            new Label("Date"), datePicker,
            new Label("Category"), categoryField,
            new Label("Amount"), amountField,
            new Label("Description"), descField,
            saveBtn
        );
        vbox.setPadding(new Insets(20));
        dialog.setScene(new Scene(vbox));
        dialog.show();
    }

    private void refresh() {
        data.setAll(expenseDao.getAllBusinessExpenses());
    }
}