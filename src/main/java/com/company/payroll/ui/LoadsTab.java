package com.company.payroll.ui;

import com.company.payroll.dao.DriverDao;
import com.company.payroll.dao.LoadDao;
import com.company.payroll.model.Driver;
import com.company.payroll.model.Load;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoadsTab extends BorderPane implements DataRefreshListener {
    private final LoadDao loadDao = new LoadDao();
    private final DriverDao driverDao = new DriverDao();

    private final ObservableList<Load> unpaidLoads = FXCollections.observableArrayList();
    private final ObservableList<Load> paidLoads = FXCollections.observableArrayList();
    private final ObservableList<Driver> driverList = FXCollections.observableArrayList();

    private TableView<Load> unpaidTable;
    private TableView<Load> paidTable;
    private ComboBox<Driver> driverCombo;

    // Listeners to notify on data change (e.g. EmployeeTab)
    private final List<DataRefreshListener> dataRefreshListeners = new ArrayList<>();

    public LoadsTab() {
        // Input Fields (for adding new only)
        TextField loadNumberField = new TextField();
        loadNumberField.setPromptText("Load #");

        TextField customerField = new TextField();
        customerField.setPromptText("Customer");

        TextField pickUpField = new TextField();
        pickUpField.setPromptText("Pick Up Location");

        TextField dropField = new TextField();
        dropField.setPromptText("Drop Location");

        driverCombo = new ComboBox<>(driverList);
        driverCombo.setPromptText("Driver");
        driverCombo.setCellFactory(listView -> new ListCell<Driver>() {
            @Override
            protected void updateItem(Driver driver, boolean empty) {
                super.updateItem(driver, empty);
                setText((driver == null || empty) ? "" : driver.getName());
            }
        });
        driverCombo.setButtonCell(new ListCell<Driver>() {
            @Override
            protected void updateItem(Driver driver, boolean empty) {
                super.updateItem(driver, empty);
                setText((driver == null || empty) ? "" : driver.getName());
            }
        });

        DatePicker deliveredDatePicker = new DatePicker(LocalDate.now());

        Spinner<Double> grossAmountSpinner = new Spinner<>(0.0, 1_000_000.0, 0.0, 1.0);
        grossAmountSpinner.setEditable(true);

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        Button addLoadBtn = new Button("Add Load");

        // Input Layout
        VBox inputBox = new VBox(10,
                new Label("Load #"), loadNumberField,
                new Label("Customer"), customerField,
                new Label("Pick Up Location"), pickUpField,
                new Label("Drop Location"), dropField,
                new Label("Driver"), driverCombo,
                new Label("Delivered Date"), deliveredDatePicker,
                new Label("Gross Amount"), grossAmountSpinner,
                new Label("Description"), descriptionField,
                addLoadBtn
        );
        inputBox.setPadding(new Insets(15));
        inputBox.setPrefWidth(280);
        inputBox.getStyleClass().add("sidebar-pane");

        // Table columns for loads - explicit binding for JavaFX TableView
        unpaidTable = createLoadsTableView();
        paidTable = createLoadsTableView();

        unpaidTable.setItems(unpaidLoads);
        paidTable.setItems(paidLoads);

        TabPane tabPane = new TabPane();
        Tab unpaidTab = new Tab("Unpaid Loads", unpaidTable);
        Tab paidTab = new Tab("Paid Loads", paidTable);
        unpaidTab.setClosable(false);
        paidTab.setClosable(false);
        tabPane.getTabs().addAll(unpaidTab, paidTab);
        tabPane.getStyleClass().add("loads-tabpane");

        // Bottom Controls
        Button editBtn = new Button("Edit Selected");
        Button deleteBtn = new Button("Delete Selected");
        HBox bottomBtns = new HBox(10, editBtn, deleteBtn);
        bottomBtns.setPadding(new Insets(8));
        bottomBtns.getStyleClass().add("bottom-bar");

        // Main Layout
        setLeft(inputBox);
        setCenter(tabPane);
        setBottom(bottomBtns);

        this.getStyleClass().add("tab-background");
        refreshDrivers(); // Load drivers at start
        refreshLoads();   // Load loads at start

        // Button Actions
        addLoadBtn.setOnAction(e -> {
            if (loadNumberField.getText().isEmpty() || driverCombo.getValue() == null || deliveredDatePicker.getValue() == null) {
                showAlert("Load #, Driver, and Delivered Date are required.");
                return;
            }
            Load newLoad = new Load();
            newLoad.setLoadNumber(loadNumberField.getText());
            newLoad.setCustomer(customerField.getText());
            newLoad.setPickupLocation(pickUpField.getText());
            newLoad.setDropLocation(dropField.getText());
            newLoad.setDriverId(driverCombo.getValue().getId());
            newLoad.setDeliveredDate(deliveredDatePicker.getValue());
            newLoad.setGrossAmount(grossAmountSpinner.getValue());
            newLoad.setDescription(descriptionField.getText());
            newLoad.setDriverPercent(driverCombo.getValue().getDriverPercent());
            newLoad.setPaid(false);
            loadDao.addLoad(newLoad);
            refreshLoads();
            notifyDataRefresh();
            // Optionally clear fields
            loadNumberField.clear();
            customerField.clear();
            pickUpField.clear();
            dropField.clear();
            driverCombo.getSelectionModel().clearSelection();
            deliveredDatePicker.setValue(LocalDate.now());
            grossAmountSpinner.getValueFactory().setValue(0.0);
            descriptionField.clear();
        });

        editBtn.setOnAction(e -> {
            Load selected = getSelectedLoad();
            if (selected != null) {
                showEditDialog(selected);
            }
        });

        deleteBtn.setOnAction(e -> {
            Load selected = getSelectedLoad();
            if (selected != null) {
                loadDao.deleteLoad(selected.getId());
                refreshLoads();
                notifyDataRefresh();
            }
        });
    }

    private TableView<Load> createLoadsTableView() {
        TableView<Load> table = new TableView<>();

        TableColumn<Load, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));

        TableColumn<Load, String> loadNumCol = new TableColumn<>("Load #");
        loadNumCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLoadNumber()));

        TableColumn<Load, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomer()));

        TableColumn<Load, String> pickUpCol = new TableColumn<>("Pick Up");
        pickUpCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPickupLocation()));

        TableColumn<Load, String> dropCol = new TableColumn<>("Drop");
        dropCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDropLocation()));

        TableColumn<Load, String> driverCol = new TableColumn<>("Driver");
        driverCol.setCellValueFactory(cellData -> {
            Driver driver = driverDao.getDriverById(cellData.getValue().getDriverId());
            return new SimpleStringProperty(driver != null ? driver.getName() : "[Missing Driver]");
        });

        TableColumn<Load, String> dateCol = new TableColumn<>("Delivered Date");
        dateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDeliveredDate();
            return new SimpleStringProperty(date != null ? date.toString() : "");
        });

        TableColumn<Load, Number> grossCol = new TableColumn<>("Gross Amount");
        grossCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGrossAmount()));

        TableColumn<Load, Number> driverPercentCol = new TableColumn<>("Driver %");
        driverPercentCol.setCellValueFactory(cellData -> {
            Driver driver = driverDao.getDriverById(cellData.getValue().getDriverId());
            return new ReadOnlyObjectWrapper<>(driver != null ? driver.getDriverPercent() : 0.0);
        });

        TableColumn<Load, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        table.getColumns().addAll(idCol, loadNumCol, customerCol, pickUpCol, dropCol, driverCol, dateCol, grossCol, driverPercentCol, descCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("main-table");
        table.setPlaceholder(new Label("No loads found."));
        return table;
    }

    private void showEditDialog(Load load) {
        Stage dialog = new Stage();
        dialog.setTitle("Edit Load");
        dialog.initModality(Modality.APPLICATION_MODAL);

        TextField loadNumberField = new TextField(load.getLoadNumber());
        TextField customerField = new TextField(load.getCustomer());
        TextField pickUpField = new TextField(load.getPickupLocation());
        TextField dropField = new TextField(load.getDropLocation());

        ComboBox<Driver> editDriverCombo = new ComboBox<>(FXCollections.observableArrayList(driverList));
        // Select the right driver
        Driver selDriver = driverList.stream().filter(d -> d.getId() == load.getDriverId()).findFirst().orElse(null);
        editDriverCombo.getSelectionModel().select(selDriver);
        editDriverCombo.setCellFactory(listView -> new ListCell<Driver>() {
            @Override
            protected void updateItem(Driver driver, boolean empty) {
                super.updateItem(driver, empty);
                setText((driver == null || empty) ? "" : driver.getName());
            }
        });
        editDriverCombo.setButtonCell(new ListCell<Driver>() {
            @Override
            protected void updateItem(Driver driver, boolean empty) {
                super.updateItem(driver, empty);
                setText((driver == null || empty) ? "" : driver.getName());
            }
        });

        DatePicker deliveredDatePicker = new DatePicker(load.getDeliveredDate());

        Spinner<Double> grossAmountSpinner = new Spinner<>(0.0, 1_000_000.0, load.getGrossAmount(), 1.0);
        grossAmountSpinner.setEditable(true);

        TextField descriptionField = new TextField(load.getDescription());

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");

        VBox vbox = new VBox(10,
                new Label("Load #"), loadNumberField,
                new Label("Customer"), customerField,
                new Label("Pick Up Location"), pickUpField,
                new Label("Drop Location"), dropField,
                new Label("Driver"), editDriverCombo,
                new Label("Delivered Date"), deliveredDatePicker,
                new Label("Gross Amount"), grossAmountSpinner,
                new Label("Description"), descriptionField,
                new HBox(10, saveBtn, cancelBtn)
        );
        vbox.setPadding(new Insets(15));

        saveBtn.setOnAction(e -> {
            if (loadNumberField.getText().isEmpty() || editDriverCombo.getValue() == null || deliveredDatePicker.getValue() == null) {
                showAlert("Load #, Driver, and Delivered Date are required.");
                return;
            }
            load.setLoadNumber(loadNumberField.getText());
            load.setCustomer(customerField.getText());
            load.setPickupLocation(pickUpField.getText());
            load.setDropLocation(dropField.getText());
            load.setDriverId(editDriverCombo.getValue().getId());
            load.setDeliveredDate(deliveredDatePicker.getValue());
            load.setGrossAmount(grossAmountSpinner.getValue());
            load.setDescription(descriptionField.getText());
            load.setDriverPercent(editDriverCombo.getValue().getDriverPercent());
            // Paid/unpaid status stays the same
            loadDao.updateLoad(load);
            refreshLoads();
            notifyDataRefresh();
            dialog.close();
        });

        cancelBtn.setOnAction(e -> dialog.close());

        Scene scene = new Scene(vbox);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    public void addDataRefreshListener(DataRefreshListener listener) {
        if (!dataRefreshListeners.contains(listener))
            dataRefreshListeners.add(listener);
    }

    private void notifyDataRefresh() {
        for (DataRefreshListener listener : dataRefreshListeners) {
            listener.refreshLoads();
        }
    }

    private Load getSelectedLoad() {
        TabPane tabPane = (TabPane) getCenter();
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab.getText().contains("Unpaid")) {
            return unpaidTable.getSelectionModel().getSelectedItem();
        } else {
            return paidTable.getSelectionModel().getSelectedItem();
        }
    }

    // Called when Employees (drivers) may have changed
    @Override
    public void refreshDrivers() {
        List<Driver> drivers = driverDao.getAllDrivers();
        driverList.setAll(drivers);
        refreshLoads(); // Ensures driver percent/name in table updates immediately
    }

    // Called when Loads may have changed (added/edited/deleted)
    @Override
    public void refreshLoads() {
        List<Load> unpaid = loadDao.getUnpaidLoads();
        List<Load> paid = loadDao.getPaidLoads();
        unpaidLoads.setAll(unpaid);
        paidLoads.setAll(paid);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Validation Error");
        alert.showAndWait();
    }
}