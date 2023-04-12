import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class TCPServerRouter {
    public static void main(String[] args) throws IOException {
        Socket clientSocket = null; // socket for the thread
        Socket sRouterSocket = null; //socket for the SRouters
        Object[][] RoutingTable = new Object[100][3]; // routing table
        int SockNum = 5555; // port number
        Boolean Running = true;
        int ind = 0; // index in the routing table
        String nickname = "";

        // Reader writer for SRouter comms
        BufferedReader serverRouterIn = null;
        PrintWriter serverRouterOut = null;
        BufferedReader clientIn = null;
        PrintWriter clientOut = null;

        // Accepting connections
        ServerSocket serverSideSocket = null; // server socket for accepting connections on the side of the server
        ServerSocket serverCommSocket = null; // socket used between Server Routers
        try {
            serverSideSocket = new ServerSocket(5555);
            System.out.println("ServerRouter is Listening on port: " + serverSideSocket.getLocalPort());

            // ServerRouter Communication Setup
            serverCommSocket = new ServerSocket(6666);
            System.out.println("ServerRouter is Listening on port: " + serverCommSocket.getLocalPort());
            sRouterSocket = serverCommSocket.accept(); // accept connection from the other SRouter
            serverRouterIn = new BufferedReader(new InputStreamReader(sRouterSocket.getInputStream()));
            serverRouterOut = new PrintWriter(sRouterSocket.getOutputStream(), true);
            serverRouterOut.println("ServerRouters are connected on port: " + serverCommSocket.getLocalPort());
            System.out.println(serverRouterIn.readLine() + "\n"); // echo from SRouter 2 to confirm connection

        } catch (IOException e) {
            System.err.println("Could not listen on port: 5555.");
            System.exit(1);
        }

        // Creating threads with accepted connections
        while (Running == true) {
            try {
                // Accept client connection w/ reader/writer
                clientSocket = serverSideSocket.accept();
                clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
                System.out.println(clientIn.readLine()+"\n"); // Confirmation of connection
                clientOut.println("Hello from SRouter1");

//                Store nickname of the current client
                nickname = clientIn.readLine();

//                Store destination from client1 and send to the other SRouter
                String destination = clientIn.readLine();
                System.out.println("Destination: " + destination);

//                Scanner sc = new Scanner(System.in);
//                System.out.println("Input: ");
//                destination = sc.nextLine();
                serverRouterOut.println(destination);

                SThread t = new SThread(RoutingTable, clientSocket, ind, nickname); // creates a thread with a random port
                t.start(); // starts the thread
                ind++; // increments the index
                System.out.println("ServerRouter connected with Client/Server: " + clientSocket.getInetAddress().getHostAddress() + "\n");

                if (serverRouterIn.readLine().equals("true")){
                    System.out.println(destination + " exists. Please wait for connection");

                    // Set socket up for the clients to use
                    clientOut.println("new ServerSocket(7777)");
                }
                else {
                    System.out.println(destination + " does not exist.");
                }
                System.out.println(serverRouterIn.readLine());

            } catch (IOException e) {
                System.err.println("Client/Server failed to connect.");
                System.exit(1);
            }
        } // end while

        // closing connections
        clientSocket.close();
        serverSideSocket.close();
    }
}
