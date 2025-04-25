package com.agenda;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ContactUI {
    private final ObservableList<Contact> contacts = FXCollections.observableArrayList();
    private final VBox root = new VBox();
    private boolean isDarkMode = false;

    public ContactUI() {
        createUI();
    }

    private void createUI() {
        TableView<Contact> tableView = new TableView<>(contacts);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Contact, String> nameCol = new TableColumn<>("Nome");
        nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());
        TableColumn<Contact, String> phoneCol = new TableColumn<>("Telefone");
        phoneCol.setCellValueFactory(cell -> cell.getValue().phoneProperty());
        tableView.getColumns().addAll(nameCol, phoneCol);

        TextField nameField = new TextField();
        nameField.setPromptText("Nome");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Telefone");
        Button addButton = new Button("Adicionar");
        Button exportButton = new Button("Exportar Excel");
        Button themeButton = new Button("Alternar Tema");
        Button editButton = new Button("Editar");
        Button deleteButton = new Button("Excluir");

        HBox form = new HBox(10, nameField, phoneField, addButton, editButton, deleteButton, exportButton, themeButton);
        form.setPadding(new Insets(10));

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                phoneField.setText(newSelection.getPhone());
            }
        });

        addButton.setOnAction(e -> {
            contacts.add(new Contact(nameField.getText(), phoneField.getText()));
            nameField.clear();
            phoneField.clear();
        });

        editButton.setOnAction(e -> {
            Contact selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setName(nameField.getText());
                selected.setPhone(phoneField.getText());
                tableView.refresh();
                nameField.clear();
                phoneField.clear();
            }
        });

        deleteButton.setOnAction(e -> {
            Contact selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                contacts.remove(selected);
            }
        });

        exportButton.setOnAction(e -> exportToExcel());
        themeButton.setOnAction(e -> toggleTheme(root.getScene()));
        root.getChildren().addAll(form, tableView);
        root.setPadding(new Insets(10));
    }

    public Pane getView() {
        return root;
    }

    private void toggleTheme(Scene scene) {
        isDarkMode = !isDarkMode;
        scene.getStylesheets().clear();
        if (isDarkMode) {
            scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
        } else {
            scene.getStylesheets().add(getClass().getResource("/light-theme.css").toExternalForm());
        }
    }

    private void exportToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Contatos");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Nome");
        header.createCell(1).setCellValue("Telefone");

        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(c.getName());
            row.createCell(1).setCellValue(c.getPhone());
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Arquivo Excel");
        fileChooser.setInitialFileName("contatos.xlsx");
        Stage stage = new Stage();
        java.io.File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}