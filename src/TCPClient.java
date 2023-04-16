import jdk.jshell.execution.Util;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

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
        String routerName = "localhost"; // ServerRouter localHost name

        int SockNum = 5555; // port number

        // Tries to connect to the ServerRouter and sets up reader/writers
        try {
            serverCommSocket = new Socket(routerName, SockNum);
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
        String fileName = "./file.txt";
//        String fileName = "A.Quiet.Place.2018.720p.BluRay.x264-[YTS.AM].mp4";
        Reader reader = new FileReader(fileName);
        BufferedReader fromFile = new BufferedReader(reader); // reader for the string file
        String fromRouter; // messages received from ServerRouter

        String destinationName = "T2"; // destination Name (Client 2)
        Scanner sc = new Scanner(System.in);
        System.out.print("Who would you like to communicate with?: ");
        destinationName = sc.nextLine();

        // Communication process (initial sends/receives)
        serverRouterOut.println(destinationName);// initial send (IP of the destination Server)
        System.out.println(serverRouterIn.readLine());
        fromRouter = serverRouterIn.readLine();// initial receive from router (verification of connection)
        System.out.println("ServerRouter: " + fromRouter);

        //Goes to SThread
        serverRouterOut.println(userName);
        serverRouterOut.println(localHost); // Client sends the IP of its machine as initial send

        // Setting up connection through the SRouter
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


        // Reader/Writer for the clientCommSocket
        clientIn = new BufferedReader(new InputStreamReader(clientCommSocket.getInputStream()));
        clientOut = new PrintWriter(clientCommSocket.getOutputStream(), true);

        // Confirm connection to other client/server
        clientOut.println("Hello from T1");

        Path path = Paths.get(fileName);
        byte[] data = Files.readAllBytes(path);

        System.out.println(clientIn.readLine());
        String encodedString = Base64.getEncoder().encodeToString(data);

        // Timer start
//        System.out.println(LocalDateTime.now());
        LocalDateTime start = LocalDateTime.now();

        clientOut.println(encodedString);
        clientOut.println("The message was sent");
        clientOut.println("Goodbye");
        System.out.println(clientIn.readLine());

        System.out.println(clientIn.readLine()); // confirm receipt of message

        // closing connections
        serverRouterOut.close();
        serverRouterIn.close();
        clientIn.close();
        clientOut.close();



    }
}