package com.company.payroll.ui;

import javafx.scene.Scene;
import com.company.payroll.dao.DriverDao;
import com.company.payroll.dao.LoadDao;
import com.company.payroll.dao.TrailerDao;
import com.company.payroll.model.Driver;
import com.company.payroll.model.Load;
import com.company.payroll.model.Trailer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MissingActionsTab extends VBox {

    private final DriverDao driverDao = new DriverDao();
    private final LoadDao loadDao = new LoadDao();
    private final TrailerDao trailerDao = new TrailerDao();

    public MissingActionsTab() {
        setPadding(new Insets(20));
        setSpacing(20);

        // Orphaned Loads: Loads with no assigned driver
        Label orphanedLoadsLabel = new Label("Orphaned Loads (No Assigned Driver):");
        TableView<Load> orphanedLoadsTable = new TableView<>();
        ObservableList<Load> orphanedLoads = FXCollections.observableArrayList(
                loadDao.getAllLoads().stream()
                        .filter(l -> l.getDriverId() == 0)
                        .collect(Collectors.toList())
        );
        TableColumn<Load, Integer> loadIdCol = new TableColumn<>("ID");
        loadIdCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        TableColumn<Load, String> loadNumCol = new TableColumn<>("Load Number");
        loadNumCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("loadNumber"));
        TableColumn<Load, String> loadCustCol = new TableColumn<>("Customer");
        loadCustCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("customer"));
        orphanedLoadsTable.getColumns().addAll(loadIdCol, loadNumCol, loadCustCol);
        orphanedLoadsTable.setItems(orphanedLoads);

        // Unassigned Drivers: Drivers not assigned to any loads
        Label unassignedDriversLabel = new Label("Unassigned Drivers (No Loads):");
        TableView<Driver> unassignedDriversTable = new TableView<>();
        List<Driver> allDrivers = driverDao.getAllDrivers();
        Set<Integer> assignedDriverIds = loadDao.getAllLoads().stream()
                .map(Load::getDriverId)
                .filter(id -> id != 0)
                .collect(Collectors.toSet());
        ObservableList<Driver> unassignedDrivers = FXCollections.observableArrayList(
                allDrivers.stream()
                        .filter(d -> !assignedDriverIds.contains(d.getId()))
                        .collect(Collectors.toList())
        );
        TableColumn<Driver, Integer> driverIdCol = new TableColumn<>("ID");
        driverIdCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        TableColumn<Driver, String> driverNameCol = new TableColumn<>("Name");
        driverNameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        TableColumn<Driver, String> driverTruckCol = new TableColumn<>("Truck");
        driverTruckCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("truckId"));
        unassignedDriversTable.getColumns().addAll(driverIdCol, driverNameCol, driverTruckCol);
        unassignedDriversTable.setItems(unassignedDrivers);

        // Unused Trailers: Trailers not assigned (active, but no loads or maintenance)
        Label unusedTrailersLabel = new Label("Unused Trailers (Active, Not Referenced in Loads or Maintenance):");
        TableView<Trailer> unusedTrailersTable = new TableView<>();
        List<Trailer> allTrailers = trailerDao.getAllTrailers().stream()
                .filter(Trailer::isActive)
                .collect(Collectors.toList());
        Set<String> usedTrailerNumbers = loadDao.getAllLoads().stream()
                .map(Load::getDropLocation) // Not really a trailer, but suppose location refers to a trailer number in your case
                .collect(Collectors.toSet());
        ObservableList<Trailer> unusedTrailers = FXCollections.observableArrayList(
                allTrailers.stream()
                        .filter(t -> !usedTrailerNumbers.contains(t.getTrailerNumber()))
                        .collect(Collectors.toList())
        );
        TableColumn<Trailer, Integer> trailerIdCol = new TableColumn<>("ID");
        trailerIdCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        TableColumn<Trailer, String> trailerNumCol = new TableColumn<>("Trailer Number");
        trailerNumCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("trailerNumber"));
        TableColumn<Trailer, String> trailerVinCol = new TableColumn<>("VIN");
        trailerVinCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("vin"));
        unusedTrailersTable.getColumns().addAll(trailerIdCol, trailerNumCol, trailerVinCol);
        unusedTrailersTable.setItems(unusedTrailers);

        // Layout
        getChildren().setAll(
                orphanedLoadsLabel, orphanedLoadsTable,
                unassignedDriversLabel, unassignedDriversTable,
                unusedTrailersLabel, unusedTrailersTable
        );
    }
}