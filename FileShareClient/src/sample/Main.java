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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Main extends Application {

    private ListView<FileData> localList = new ListView<>();
    private ListView<String> serverList = new ListView<>();

    private FileData localSelected = null;
    private String serverSelected = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();

        // Local folder
        File localFolder = new File("shared");
        localFolder.mkdirs();

        ObservableList<FileData> files = FXCollections.observableArrayList();
        for (File file : localFolder.listFiles()) {
            // Read the input to data
            String data = "";
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                data += currentLine + "%n";
            }

            FileData newData = new FileData(file.getName(), data);
            files.add(newData);
        }

        localList.setItems(files);
        localList.setEditable(true);
        localList.setMinWidth(200);
        localList.setMinHeight(500);
        localList.setCellFactory(new Callback<ListView<FileData>, ListCell<FileData>>() {
            @Override
            public ListCell<FileData> call(ListView<FileData> list) {
                ListCell<FileData> cell = new ListCell<FileData>() {
                    @Override
                    protected void updateItem(FileData data, boolean bln) {
                        super.updateItem(data, bln);
                        if (data != null) {
                            setText(data.getFilename());
                        }
                    }
                };
                return cell;
            }
        });

        localList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked on " + localList.getSelectionModel().getSelectedItem().getFilename());
                localSelected = localList.getSelectionModel().getSelectedItem();
            }
        });


        // Server folder
        // Call dir on startup to get all the files in the server shared folder
        FileShareClient fsc = new FileShareClient();
        ObservableList<String> serverFiles = FXCollections.observableArrayList();
        fsc.Dir(serverFiles);

        serverList.setItems(serverFiles);
        serverList.setEditable(true);
        serverList.setMinWidth(200);
        serverList.setMinHeight(500);

        // Buttons
        Button dirButton = new Button("Dir");
        dirButton.setMinWidth(100);

        Button downloadButton = new Button("Download");
        downloadButton.setMinWidth(100);

        Button uploadButton = new Button("Upload");
        uploadButton.setMinWidth(100);
        uploadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println("Uploading " + localSelected.getFilename() + "...");
                FileShareClient fsc = new FileShareClient();
                fsc.Upload(localSelected.getFilename(), localSelected.getData());
                fsc = new FileShareClient();
                fsc.Dir(serverFiles);
                System.out.println("Uploaded successfully to " + fsc.SERVER_ADDRESS + ":" + fsc.SERVER_PORT);
            }
        });

        // Layout
        HBox buttonsHBox = new HBox();
        buttonsHBox.setSpacing(0);
        buttonsHBox.getChildren().

                addAll(dirButton, downloadButton, uploadButton);

        HBox listHBox = new HBox();
        listHBox.setSpacing(5);
        listHBox.setPadding(new

                Insets(10, 0, 0, 0)

        );
        listHBox.getChildren().addAll(localList, serverList);

        VBox vBox = new VBox();
        vBox.setSpacing(0);
        vBox.getChildren().

                addAll(buttonsHBox, listHBox);

        root.getChildren().

                addAll(vBox);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new

                Scene(root, 500, 600)

        );
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
