package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main extends Application {

    private ListView<String> localList = new ListView<>();
    private ListView<String> serverList = new ListView<>();
    private ObservableList<String> localFiles = FXCollections.observableArrayList();
    private ObservableList<String> serverFiles = FXCollections.observableArrayList();
    private String localSelected = null;
    private String serverSelected = null;
    private File localFolder = new File("shared");

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();

        // generate a shared folder
        localFolder.mkdirs();

        // Local folder
        refreshLocalList();
        localList.setItems(localFiles);
        localList.setEditable(true);
        localList.setMinWidth(200);
        localList.setMinHeight(500);
        localList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                localSelected = localList.getSelectionModel().getSelectedItem();
            }
        });

        // Server folder
        // Call dir on startup to get all the files in the server shared folder
        FileShareClient fsc = new FileShareClient();
        fsc.Dir(serverFiles);
        serverList.setItems(serverFiles);
        serverList.setEditable(true);
        serverList.setMinWidth(200);
        serverList.setMinHeight(500);
        serverList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                serverSelected = serverList.getSelectionModel().getSelectedItem();
            }
        });

        // Buttons
        Button downloadButton = new Button("Download");
        downloadButton.setMinWidth(100);
        downloadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (serverSelected != null) {
                    System.out.println("Downloading " + serverSelected + "...");
                    FileShareClient fsc = new FileShareClient();
                    fsc.Download(localFolder.getAbsolutePath(), serverSelected);
                    refreshLocalList();
                }
            }
        });

        Button uploadButton = new Button("Upload");
        uploadButton.setMinWidth(100);
        uploadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (localSelected != null) {
                    System.out.println("Uploading " + localSelected + "...");
                    FileShareClient fsc = new FileShareClient();
                    fsc.Upload(localFolder.getAbsolutePath(), localSelected);
                    fsc = new FileShareClient();

                    fsc.Dir(serverFiles);
                }
            }
        });

        // Layout
        HBox buttonsHBox = new HBox();
        buttonsHBox.setSpacing(0);
        buttonsHBox.getChildren().addAll(downloadButton, uploadButton);

        HBox listHBox = new HBox();
        listHBox.setSpacing(5);
        listHBox.setPadding(new Insets(10, 0, 0, 0));
        listHBox.getChildren().addAll(localList, serverList);

        VBox vBox = new VBox();
        vBox.setSpacing(0);
        vBox.getChildren().addAll(buttonsHBox, listHBox);

        root.getChildren().addAll(vBox);
        primaryStage.setTitle("File Share Client. By Rudy Lee");
        primaryStage.setScene(new Scene(root, 500, 600));
        primaryStage.show();
    }

    protected void refreshLocalList() {
        localFiles.clear();
        for (File file : localFolder.listFiles()) {
            localFiles.add(file.getName());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
