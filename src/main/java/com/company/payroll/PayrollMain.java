package com.company.payroll;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import com.company.payroll.ui.*;

public class PayrollMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();

        // Create tabs
        EmployeeTab employeeTab = new EmployeeTab();
        LoadsTab loadsTab = new LoadsTab();

        // Register cross-tab data refresh listeners
        employeeTab.addDataRefreshListener(loadsTab); // LoadsTab should refresh drivers when Employees change
        loadsTab.addDataRefreshListener(employeeTab); // EmployeeTab could refresh if Loads change (optional)

        tabPane.getTabs().add(new Tab("Employees", employeeTab));
        tabPane.getTabs().add(new Tab("Loads", loadsTab));
        tabPane.getTabs().add(new Tab("Fuel Import", new FuelImportTab()));
        tabPane.getTabs().add(new Tab("Payroll", new PayrollTab()));
        tabPane.getTabs().add(new Tab("Company Income", new CompanyIncomeTab()));
        tabPane.getTabs().add(new Tab("Business Expenses", new BusinessExpenseTab()));
        tabPane.getTabs().add(new Tab("Maintenance", new MaintenanceTab()));
        tabPane.getTabs().add(new Tab("Trailers", new TrailerTab()));
        tabPane.getTabs().add(new Tab("Driver Income Summary", new DriverIncomeTab()));
        tabPane.getTabs().add(new Tab("Audit", new AuditTab()));
        tabPane.getTabs().add(new Tab("Missing Actions", new MissingActionsTab()));

        Scene scene = new Scene(tabPane, 1200, 750);

        primaryStage.setTitle("Semi-Truck Payroll System");
        primaryStage.setScene(scene);
        try {
            primaryStage.getIcons().add(new Image("assets/icon/mfg_logo.png"));
        } catch (Exception e) {
            // icon is optional
        }
        primaryStage.show();
    }

    public static void main(String[] args) {
        Database.init();
        launch(args);
    }
}