import java.io.*;
import java.net.*;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TCPClient {
    public static void main(String[] args) throws IOException {

        // Variables for setting up connection and communication
        Socket serverCommSocket = null; // socket to connect with ServerRouter
        Socket clientCommSocket = null; // socket to connect clients together
        PrintWriter serverRouterOut = null; // for writing to ServerRouter
        BufferedReader serverRouterIn = null; // for reading form ServerRouter
        PrintWriter clientOutToSRouter = null; // for writing to ServerRouter
        BufferedReader clientInFromSRouter = null; // for reading form ServerRouter
        PrintWriter clientOut = null; // for writing to ServerRouter
        BufferedReader clientIn = null; // for reading form ServerRouter

        InetAddress addr = InetAddress.getLocalHost();
        String localHost = addr.getHostAddress(); // Client machine's IP
        // name of client to be put in RTable
        String userName = "T1";

        // IP for my local server router machine
        String routerName = "192.168.1.71"; // ServerRouter localHost name

        int SockNum = 5555; // port number

        // Tries to connect to the ServerRouter
        try {
//            Socket = new Socket(routerName, SockNum);
            serverCommSocket = new Socket("localhost", SockNum);
            serverRouterOut = new PrintWriter(serverCommSocket.getOutputStream(), true);
            serverRouterIn = new BufferedReader(new InputStreamReader(serverCommSocket.getInputStream()));
            serverRouterOut.println("Hello from client 1 AKA " + userName);
            serverRouterOut.println(userName);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about router: " + routerName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + routerName);
            System.exit(1);
        }

        // Variables for message passing
//        String fileName = "./CantinaBand3.wav";
//        String fileName = "./file.txt";
        String fileName = "./Lecture10-video.mp4";
        Reader reader = new FileReader(fileName);
        BufferedReader fromFile = new BufferedReader(reader); // reader for the string file
        String fromRouter; // messages received from ServerRouter
        String fromUser; // messages sent to ServerRouter
        String address ="192.168.1.71";// Local IP for the server
        String destinationName = "T2"; // destination Name (Client 2)

        long t0, t1, t;

        // Variables for calculations and gathering data
        int msgCount = 0, totalMsgSize = 0, totalTransmissionSize = 0;

        // Communication process (initial sends/receives)
        serverRouterOut.println(destinationName);// initial send (IP of the destination Server)
        fromRouter = serverRouterIn.readLine();// initial receive from router (verification of connection)
        System.out.println("ServerRouter: " + fromRouter);

        //Goes to SThread
        serverRouterOut.println(userName);
        serverRouterOut.println(localHost); // Client sends the IP of its machine as initial send

        // Setting up connection through the SRouter
        System.out.println(serverRouterIn.readLine());
        String destinationClientIP = serverRouterIn.readLine();
        int portNum = Integer.parseInt(serverRouterIn.readLine());
        try {
            clientCommSocket = new Socket(destinationClientIP, portNum);

            if (clientCommSocket.isConnected()) {
                serverRouterOut.close();
                serverRouterIn.close();
                serverCommSocket.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // for client2
        serverRouterOut.println(fileName);
        t0 = System.currentTimeMillis();

        // Reader/Writer for the clientCommSocket
        clientIn = new BufferedReader(new InputStreamReader(clientCommSocket.getInputStream()));
        clientOut = new PrintWriter(clientCommSocket.getOutputStream(), true);

        clientOut.println("hello");

        Path path = Paths.get(fileName);
        byte[] data = Files.readAllBytes(path);

        System.out.println(clientIn.readLine());
        System.out.println(clientIn.readLine());
        System.out.println(clientIn.readLine());
        String encodedString = Base64.getEncoder().encodeToString(data);

        // Timer start
        clientOut.println(encodedString);

                // Stores the metrics gathered and performs some final calculations
        double avgMessageSize = 0, avgTransmissionTime = 0;
        if (msgCount > 0) {
            avgMessageSize = (double) totalMsgSize / msgCount; // Calculating average message size
            avgTransmissionTime = (double) totalTransmissionSize / msgCount;
        }

        // Writing statistical results to results.txt
        try {
            FileWriter resultFW = new FileWriter("results.txt");
            BufferedWriter resultBW = new BufferedWriter(resultFW);
            resultBW.write("Average message size is: " + avgMessageSize);
            resultBW.newLine();
            resultBW.write("Average transmission time is: " + avgTransmissionTime);
            resultBW.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }

        // closing connections
        serverRouterOut.close();
        serverRouterIn.close();



    }
}