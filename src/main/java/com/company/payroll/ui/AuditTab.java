package com.company.payroll.ui;

import javafx.scene.Scene;
import com.company.payroll.dao.AuditLogDao;
import com.company.payroll.model.AuditLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDateTime;

public class AuditTab extends BorderPane {
    private TableView<AuditLog> table;
    private ObservableList<AuditLog> data;
    private AuditLogDao auditDao = new AuditLogDao();

    public AuditTab() {
        data = FXCollections.observableArrayList(auditDao.getAllAuditLogs());
        table = new TableView<>(data);

        TableColumn<AuditLog, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<AuditLog, LocalDateTime> tsCol = new TableColumn<>("Timestamp");
        tsCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        TableColumn<AuditLog, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));

        TableColumn<AuditLog, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("actionType"));

        TableColumn<AuditLog, String> entityCol = new TableColumn<>("Entity");
        entityCol.setCellValueFactory(new PropertyValueFactory<>("entityType"));

        TableColumn<AuditLog, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));

        table.getColumns().addAll(idCol, tsCol, userCol, actionCol, entityCol, detailsCol);

        setCenter(table);
        setPadding(new Insets(10));
    }
}