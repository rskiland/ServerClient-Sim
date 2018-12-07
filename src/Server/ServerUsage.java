// Rob Skiland - 10186568
// CISC 435 - Computer Networks F18
// Course Project
// December 6, 2018

// ServerUsage class
// Used as a synchronized data structure between all client threads to track data usage and connected users.
package Server;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

// ----- ServerUsage class -----
public class ServerUsage {

    // HashMap of client
    // Key: client remote socket address
    // Values: client name, client access number, client used requests
    private HashMap<SocketAddress, String[]> clientList;

    // ArrayList of client requests
    // Values: Datetime, accepted/failed, client name, requested file
    private ArrayList<String[]> requestList;

    // Counting the number of connected clients
    private int connectedUsers = 0;

    // ServerUsage Constructor
    public ServerUsage(){
        clientList = new HashMap<>();
        requestList = new ArrayList<>();
    }

    // ---- clientList Functions -----
    // Place data into the clientList
    public synchronized void putClientData(SocketAddress clientAddress, String[] clientData){
        clientList.put(clientAddress, clientData);
    }

    // Grab data from the clientList
    public synchronized String[] getClientData(SocketAddress clientAddress){
        if (clientList.containsKey(clientAddress)){
            return clientList.get(clientAddress);
        }
        else{
            return new String[]{};
        }
    }

    // Check if a client exists in clientList (init check)
    public synchronized boolean containsClient(SocketAddress clientAddress){
        if (clientList.containsKey(clientAddress)){
            return true;
        }
        else {
            return false;
        }
    }

    // Check if a client name is unique in clientList  (init check)
    public synchronized  boolean uniqueClientName(String clientName){
        for (HashMap.Entry<SocketAddress, String[]> entry : clientList.entrySet()) {
            if (entry.getValue()[0].equals(clientName)){
                return false;
            }
        }
        return true;
    }

    // Return the current state of clientList   (client usage stats)
    public synchronized HashMap<SocketAddress, String[]> getCompleteList(){
        return clientList;
    }


    // ----- requestUsers Functions -----
    public synchronized void appendRequestList(String[] requestParams){
        requestList.add(requestParams);
    }

    public synchronized ArrayList<String[]> returnRequestList(){
        ArrayList<String[]> tempRequestList = (ArrayList<String[]>) requestList.clone();
        return tempRequestList;
    }

    // ----- connectedUsers Functions -----
    // Get number of currently connected users
    public synchronized int getConnectedUsers(){
        return connectedUsers;
    }

    // Add one connected users
    public synchronized void addConnectedUsers(){
        connectedUsers++;
    }

    // Remove one connected users
    public synchronized void removeConnectedUsers(){
        connectedUsers--;
    }
} // end of ServerUsage
