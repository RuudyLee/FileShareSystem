package FileShareServer;

import java.io.*;
import java.net.*;
import java.util.*;

public class FileShareServerThread extends Thread {
    protected File folder;
    protected Socket socket = null;
    protected PrintWriter out = null;
    protected BufferedReader in = null;

    protected Vector messages = null;

    public FileShareServerThread(File folder, Socket socket, Vector messages) {
        super();
        this.folder = folder;
        this.socket = socket;
        this.messages = messages;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOEXception while opening a read/write connection");
        }
    }

    public void run() {
        // initialize interaction
        out.println("File Share Server, Copyright my balls");
        out.println("Awaiting incoming connections...");

        boolean endOfSession = false;
        while (!endOfSession) {
            endOfSession = processCommand();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected boolean processCommand() {
        String message = null;
        try {
            message = in.readLine();
        } catch (IOException e) {
            System.err.println("Error reading command from socket.");
            return true;
        }
        if (message == null) {
            return true;
        }

        // separate command and data
        String command = message.substring(0, message.indexOf(" "));
        String data = message.substring(message.indexOf(" ") + 1, message.length());

        return processCommand(command, data);
    }

    protected boolean processCommand(String command, String data) {

        try {
            if (command.equalsIgnoreCase("UL")) { // upload
                String filename = data.substring(0, data.indexOf(" "));
                PrintWriter writer = new PrintWriter(folder.getAbsolutePath() + "/" + filename, "UTF-8");
                writer.printf(data.substring(data.indexOf(" ") + 1, data.length()));
                writer.close();
                System.out.println(filename + " has been written to!");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}