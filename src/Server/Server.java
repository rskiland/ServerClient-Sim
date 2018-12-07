// Rob Skiland - 10186568
// CISC 435 - Computer Networks F18
// Course Project
// December 6, 2018

// Server class
// Handles all incoming clients. All new clients will be placed in their own ServerHandler thread.
package Server;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int portNumber;
    ServerUsage serverUsage;

    // Server constructor
    public Server(int portNumber){
        this.portNumber = portNumber;
        serverUsage = new ServerUsage();
    }

    // ----- start class start -----
    //public void run(int portNumber, boolean allowTextLogs) throws IOException{
    public void start(boolean allowTextLogs) throws IOException{
        // Used to allow server text logs
        if (allowTextLogs){
            if (new File("logs").exists()) {
                System.setOut(new PrintStream("logs/server-log.txt"));
            }
            else{
                new File("logs").mkdirs();
                System.setOut(new PrintStream("logs/server-log.txt"));
            }
        }

        if (!new File("cache").exists()){
            System.out.println("[SERVER] Cache does not exist. Server can't start.");
            System.exit(2);
        }
        try {
            // Create new server socket on portNumber
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("[SERVER] Starting on port " + portNumber);

            // Infinite loop to catch new client connections and create a new thread for each client
            while (true) {
                Socket socket = null;
                try {
                    // Accept new clients through socket
                    socket = serverSocket.accept();

                    // Write to server a new client is trying to connect
                    System.out.println("[SERVER] [CONNECTION] " + socket.getRemoteSocketAddress());

                    // Count number of connected users
                    if (serverUsage.getConnectedUsers() < 3) {

                        //Increase the number of connected users
                        serverUsage.addConnectedUsers();

                        //System.out.println(serverUsage.getConnectedUsers());

                        // Create object streams for clients
                        ObjectInputStream dis = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream dos = new ObjectOutputStream(socket.getOutputStream());

                        // Create new thread based on the ServerHandler class
                        Thread t = new ServerHandler(socket, dis, dos, serverUsage);

                        // Start new client thread
                        t.start();
                    }
                    // Refuse client connection if the server is at capacity        (capacity = 3)
                    else {
                        System.out.println("[SERVER] [CLOSE] " + socket.getRemoteSocketAddress() + " connection closed. Server at capacity.");
                        socket.close();
                    }
                } catch (Exception e) {
                    socket.close();
                }
            }
        }catch (BindException be) {
            System.out.println("[SERVER] Server can't start. Port " + portNumber + " already bound to process.");
        }
    }
} // End of Server class