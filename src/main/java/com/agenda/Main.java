package com.agenda;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        ContactUI contactUI = new ContactUI();
        Scene scene = new Scene(contactUI.getView(), 800, 400);
        primaryStage.setTitle("Agenda de Contatos");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}