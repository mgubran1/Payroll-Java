package com.company.payroll.ui;

import com.company.payroll.model.Load;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;

public class LoadsTestApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        TableView<Load> table = new TableView<>();
        ObservableList<Load> data = FXCollections.observableArrayList(
            new Load(1, "A123", "CustomerA", "FromA", "ToA", 1, LocalDate.now(), 100.0, 20.0, "desc1", false),
            new Load(2, "B456", "CustomerB", "FromB", "ToB", 2, LocalDate.now(), 200.0, 25.0, "desc2", true)
        );

        TableColumn<Load, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Load, String> loadNumCol = new TableColumn<>("Load #");
        loadNumCol.setCellValueFactory(new PropertyValueFactory<>("loadNumber"));

        TableColumn<Load, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customer"));

        TableColumn<Load, Integer> driverIdCol = new TableColumn<>("Driver ID");
        driverIdCol.setCellValueFactory(new PropertyValueFactory<>("driverId"));

        table.getColumns().addAll(idCol, loadNumCol, customerCol, driverIdCol);
        table.setItems(data);

        primaryStage.setScene(new Scene(table, 800, 400));
        primaryStage.setTitle("TableView Minimal Test");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}