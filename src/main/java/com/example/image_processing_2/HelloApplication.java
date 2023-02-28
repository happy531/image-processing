package com.example.image_processing_2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage s) throws IOException
    {
        s.setTitle("Image processing");

        Menu fileMenu = new Menu("File");

        MenuItem openBtn = new MenuItem("Open");
        fileMenu.getItems().add(openBtn);

        MenuItem saveBtn = new MenuItem("Save");
        fileMenu.getItems().add(saveBtn);

        Menu editMenu = new Menu("Edit");

        MenuItem undoBtn = new MenuItem("Undo");
        editMenu.getItems().add(undoBtn);

        MenuItem alg1 = new MenuItem("Alg 1");
        editMenu.getItems().add(alg1);

        MenuBar mb = new MenuBar();
        mb.getMenus().add(fileMenu);
        mb.getMenus().add(editMenu);

        VBox vb = new VBox(mb);
        Scene sc = new Scene(vb, 750, 500);

        s.setScene(sc);
        s.show();
    }

    public static void main(String[] args) {
        launch();
    }
}