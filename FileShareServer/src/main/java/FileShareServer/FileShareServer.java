package FileShareServer;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class FileShareServer {
    protected File sharedFolder = null;
    protected Socket clientSocket = null;
    protected ServerSocket serverSocket = null;
    protected FileShareServerThread[] threads = null;
    protected int numClients = 0;

    public static int SERVER_PORT = 8888;
    public static int MAX_CLIENTS = 25;

    public FileShareServer() {
        System.out.println("File Share Server! By Rudy Lee");
        sharedFolder = new File("shared");
        sharedFolder.mkdirs();

        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server Socket created");
            threads = new FileShareServerThread[MAX_CLIENTS];
            while (true) {
                System.out.println("Awaiting connection...");
                clientSocket = serverSocket.accept();
                System.out.println("Client #" + (numClients + 1) + " connected.");
                threads[numClients] = new FileShareServerThread(sharedFolder, clientSocket);
                threads[numClients].start();
                numClients++;
            }
        } catch (IOException e) {
            System.err.println("IOException while creating server connection");
        }

        try {
            serverSocket.close();
            System.out.println("server socket closed successfully");
        } catch (IOException e) {
            System.err.println("IOException while closing server connection");
        }
    }

    public static void main(String[] args) {
        FileShareServer app = new FileShareServer();
    }
}
