package com.company.payroll.ui;

import javafx.scene.Scene;
import com.company.payroll.dao.TrailerDao;
import com.company.payroll.model.Trailer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class TrailerTab extends BorderPane {
    private TableView<Trailer> table;
    private ObservableList<Trailer> data;
    private TrailerDao trailerDao = new TrailerDao();

    public TrailerTab() {
        data = FXCollections.observableArrayList(trailerDao.getAllTrailers());
        table = new TableView<>(data);

        TableColumn<Trailer, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Trailer, String> trailerNumCol = new TableColumn<>("Trailer Number");
        trailerNumCol.setCellValueFactory(new PropertyValueFactory<>("trailerNumber"));

        TableColumn<Trailer, String> vinCol = new TableColumn<>("VIN");
        vinCol.setCellValueFactory(new PropertyValueFactory<>("vin"));

        TableColumn<Trailer, String> plateNumCol = new TableColumn<>("Plate");
        plateNumCol.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));

        TableColumn<Trailer, LocalDate> plateExpCol = new TableColumn<>("Plate Expiry");
        plateExpCol.setCellValueFactory(new PropertyValueFactory<>("plateExpiry"));

        TableColumn<Trailer, String> makeCol = new TableColumn<>("Make");
        makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));

        TableColumn<Trailer, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Trailer, Boolean> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));

        table.getColumns().addAll(idCol, trailerNumCol, vinCol, plateNumCol, plateExpCol, makeCol, modelCol, activeCol);

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> showTrailerDialog(null));

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> {
            Trailer t = table.getSelectionModel().getSelectedItem();
            if (t != null) showTrailerDialog(t);
        });

        Button delBtn = new Button("Delete");
        delBtn.setOnAction(e -> {
            Trailer t = table.getSelectionModel().getSelectedItem();
            if (t != null) {
                trailerDao.deleteTrailer(t.getId());
                refresh();
            }
        });

        HBox controls = new HBox(10, addBtn, editBtn, delBtn);
        controls.setPadding(new Insets(10));

        setCenter(table);
        setBottom(controls);
    }

    private void showTrailerDialog(Trailer t) {
        Stage dialog = new Stage();
        dialog.setTitle(t == null ? "Add Trailer" : "Edit Trailer");

        TextField trailerNumberField = new TextField(t == null ? "" : t.getTrailerNumber());
        TextField vinField = new TextField(t == null ? "" : t.getVin());
        TextField plateNumberField = new TextField(t == null ? "" : t.getPlateNumber());
        DatePicker plateExpPicker = new DatePicker(t == null ? null : t.getPlateExpiry());
        TextField makeField = new TextField(t == null ? "" : t.getMake());
        TextField modelField = new TextField(t == null ? "" : t.getModel());
        CheckBox activeBox = new CheckBox("Active");
        activeBox.setSelected(t == null ? true : t.isActive());

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                if (t == null) {
                    Trailer tr = new Trailer();
                    tr.setTrailerNumber(trailerNumberField.getText());
                    tr.setVin(vinField.getText());
                    tr.setPlateNumber(plateNumberField.getText());
                    tr.setPlateExpiry(plateExpPicker.getValue());
                    tr.setMake(makeField.getText());
                    tr.setModel(modelField.getText());
                    tr.setActive(activeBox.isSelected());
                    trailerDao.addTrailer(tr);
                } else {
                    t.setTrailerNumber(trailerNumberField.getText());
                    t.setVin(vinField.getText());
                    t.setPlateNumber(plateNumberField.getText());
                    t.setPlateExpiry(plateExpPicker.getValue());
                    t.setMake(makeField.getText());
                    t.setModel(modelField.getText());
                    t.setActive(activeBox.isSelected());
                    trailerDao.updateTrailer(t);
                }
                refresh();
                dialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox vbox = new VBox(10,
            new Label("Trailer Number"), trailerNumberField,
            new Label("VIN"), vinField,
            new Label("Plate Number"), plateNumberField,
            new Label("Plate Expiry"), plateExpPicker,
            new Label("Make"), makeField,
            new Label("Model"), modelField,
            activeBox,
            saveBtn
        );
        vbox.setPadding(new Insets(20));
        dialog.setScene(new Scene(vbox));
        dialog.show();
    }

    private void refresh() {
        data.setAll(trailerDao.getAllTrailers());
    }
}