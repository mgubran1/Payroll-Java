package com.company.payroll.ui;

import com.company.payroll.dao.DriverDao;
import com.company.payroll.model.Driver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class DriverRepository {
    private static DriverRepository instance;
    private final DriverDao driverDao = new DriverDao();
    private final ObservableList<Driver> driverList = FXCollections.observableArrayList();

    private DriverRepository() {}

    public static DriverRepository getInstance() {
        if (instance == null) {
            instance = new DriverRepository();
        }
        return instance;
    }

    public ObservableList<Driver> getDriverList() {
        return driverList;
    }

    public void refreshDriversFromDatabase() {
        List<Driver> drivers = driverDao.getAllDrivers().stream()
                .filter(Driver::isActive)
                .collect(Collectors.toList());
        driverList.setAll(drivers);
    }
}