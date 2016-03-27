package sample;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Dir() {

    }

    public void Upload(String filename, String message) {
        networkOut.println("UL " + filename + " " + message);
        CloseSocket();
    }
}
