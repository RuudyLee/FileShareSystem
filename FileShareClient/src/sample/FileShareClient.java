package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;

public class FileShareClient extends Frame {
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter networkOut = null;
    private BufferedReader networkIn = null;

    public static String SERVER_ADDRESS = "localhost";
    public static int SERVER_PORT = 8888;

    public FileShareClient() {
        // Connect the socket
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + SERVER_ADDRESS);
        } catch (IOException e) {
            System.err.println("IOException while connecting to server: " + SERVER_ADDRESS);
        }

        // open read/write connection
        try {
            networkOut = new PrintWriter(socket.getOutputStream(), true);
            networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOException while opening a read/write connection");
        }

        in = new BufferedReader(new InputStreamReader(System.in));
    }

    protected void CloseSocket() {
        // Close socket
        try {
            socket.close();
            System.out.println("Socket closed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Dir(ObservableList<String> serverFiles) {
        networkOut.println("DIR");
        String buffer = null;
        String[] filenames = null;

        try {
            buffer = networkIn.readLine();
            filenames = buffer.split("\\s+");
        } catch (IOException e) {
            System.err.println("Error reading from socket.");
        }

        // Re-populate the list of filenames on server
        serverFiles.clear();
        for (String s : filenames) {
            serverFiles.add(s);
        }

        CloseSocket();
    }

    public void Upload(String path, String filename) {
        String data = "";

        try {
            FileReader fileReader = new FileReader(new File(path + "/" + filename));
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                data += currentLine + "%n";
            }
        } catch (IOException e) {
            System.err.println("IOException while opening a read connection");
        }

        networkOut.println("UL " + filename + " " + data);
        CloseSocket();
    }

    public void Download(String path, String filename) {
        networkOut.println("DL " + filename);
        String buffer = "";
        File sharedFolder = new File(path);

        try {
            String line = null;
            while ((line = networkIn.readLine()) != null) {
                buffer += line + "%n";
            }
        } catch (IOException e) {
            System.err.println("Error reading from socket.");
        }

        try {
            PrintWriter writer = new PrintWriter(path + "/" + filename, "UTF-8");
            writer.printf(buffer);
            writer.close();
            System.out.println(filename + " has been downloaded to local folder!");
        } catch (IOException e) {
            System.err.println("Error writing to file.");
        }

        CloseSocket();
    }
}
