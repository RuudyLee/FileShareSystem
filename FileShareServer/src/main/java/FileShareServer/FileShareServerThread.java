package FileShareServer;

import java.io.*;
import java.net.*;
import java.util.*;

public class FileShareServerThread extends Thread {
    protected File folder;
    protected Socket socket = null;
    protected PrintWriter out = null;
    protected BufferedReader in = null;

    public FileShareServerThread(File folder, Socket socket) {
        super();
        this.folder = folder;
        this.socket = socket;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOException while opening a read/write connection");
        }
    }

    public void run() {
        // initialize interaction
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
        String command = null;
        String data = null;
        if(message.indexOf(" ") > 0) {
            command = message.substring(0, message.indexOf(" "));
            data = message.substring(message.indexOf(" ") + 1, message.length());
        }
        else {
            command = message;
            data = null;
        }

        return processCommand(command, data);
    }

    protected boolean processCommand(String command, String data) {
        System.out.println("command: " + command);
        try {
            if (command.equalsIgnoreCase("UL")) { // upload
                // Download the received data to shared folder
                String filename = data.substring(0, data.indexOf(" "));
                PrintWriter writer = new PrintWriter(folder.getAbsolutePath() + "/" + filename, "UTF-8");
                writer.printf(data.substring(data.indexOf(" ") + 1, data.length()));
                writer.close();
                System.out.println(filename + " has been written to!");
                return true;
            } else if (command.equalsIgnoreCase("DL")) { // download
                // Output the contents of the desired file
                String filename = data;
                String message = "";
                FileReader fileReader = new FileReader(folder.getAbsolutePath() + "/" + filename);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    message += line + "%n";
                }
                out.println(message);
                return true;
            } else if (command.equalsIgnoreCase("DIR")) { // dir
                // Output a list of filenames
                String message = "";
                for(File file : folder.listFiles()) {
                    message += file.getName() + " ";
                }


                // get rid of the last space
                message = message.trim();
                System.out.println(message);
                out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
