import java.net.*;
import java.io.*;
import java.util.Arrays;

public class TCPServerRouter2 {
    public static void main(String[] args) throws IOException {
        Socket clientSocket = null; // socket for the thread
        Socket sRouterSocket = null; // socket for the Srouters
        Object[][] RoutingTable = new Object[100][3]; // routing table
        int SockNum = 5555; // port number
        Boolean Running = true;
        int ind = 0; // index in the routing table
        String nickname = "";
        String destinationFromSRouter1 = null;

        // Reader writer for SRouter comms
        BufferedReader serverRouterIn = null;
        PrintWriter serverRouterOut = null;
        BufferedReader clientIn = null;
        PrintWriter clientOut = null;

        // Accepting connections
        ServerSocket serverSideSocket = null; // server socket for accepting connections
        try {
            serverSideSocket = new ServerSocket(5556);
            System.out.println("ServerRouter2 is listening on port: "+ serverSideSocket.getLocalPort());

//          ServerRouter Communication Setup
            sRouterSocket = new Socket("192.168.1.71", 6666); // Request connection to other SRouter
            serverRouterIn = new BufferedReader(new InputStreamReader(sRouterSocket.getInputStream()));
            serverRouterOut = new PrintWriter(sRouterSocket.getOutputStream(), true);
            System.out.println(serverRouterIn.readLine());
//            out.println("ServerRouters are connected on port: " + sRouterSocket.getPort() + "\n");
            serverRouterOut.println("Hello from SRouter 2");
            destinationFromSRouter1 = serverRouterIn.readLine();

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
                clientOut.println("Hello from SRouter2");

//                Store nickname of the current client
                nickname = clientIn.readLine();

                SThread t = new SThread(RoutingTable, clientSocket, ind, nickname); // creates a thread with a random port
                t.start(); // starts the thread
                ind++; // increments the index
                System.out.println(
                        "ServerRouter connected with Client/Server: " + clientSocket.getInetAddress().getHostAddress());

                if (t.lookupNickname(destinationFromSRouter1)) {
                    System.out.println(destinationFromSRouter1 + " exists in the RTable");
                }
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
