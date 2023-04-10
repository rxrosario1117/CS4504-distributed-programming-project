import java.io.*;
import java.net.*;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TCPClient2 {
    public static void main(String[] args) throws IOException {

        // Variables for setting up connection and communication
        Socket Socket = null; // socket to connect with ServerRouter
        PrintWriter out = null; // for writing to ServerRouter
        BufferedReader in = null; // for reading form ServerRouter
        InetAddress addr = InetAddress.getLocalHost();
        String localHost = addr.getHostAddress(); // Client machine's IP

        // IP for my local server router machine
        String routerName = "192.168.4.34"; // ServerRouter localHost name

        int SockNum = 5555; // port number

        // Tries to connect to the ServerRouter
        try {
            Socket = new Socket(routerName, SockNum);
            out = new PrintWriter(Socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about router: " + routerName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + routerName);
            System.exit(1);
        }

        // Variables for message passing
        String fileName = "./CantinaBand3.wav";
        Reader reader = new FileReader(fileName);
        BufferedReader fromFile = new BufferedReader(reader); // reader for the string file
        String fromServer; // messages received from ServerRouter
        String fromUser; // messages sent to ServerRouter
        // Local IP for the server
        String address = "192.168.4.48"; // destination IP (Server)

        long t0, t1, t;

        // Variables for calculations and gathering data
        int msgCount = 0, totalMsgSize = 0, totalTransmissionSize = 0;

        // Communication process (initial sends/receives)
        out.println(address);// initial send (IP of the destination Server)
        fromServer = in.readLine();// initial receive from router (verification of connection)
        System.out.println("ServerRouter: " + fromServer);
        out.println(localHost); // Client sends the IP of its machine as initial send
        out.println(fileName);
        System.out.println("Here");
        t0 = System.currentTimeMillis();

        // Communication while loop
        while ((fromServer = in.readLine()) != null) {
            System.out.println("Server: " + fromServer);
            t1 = System.currentTimeMillis();
            if (!fileName.contains(".txt")) {
                Path path = Paths.get(fileName);
                byte[] data = Files.readAllBytes(path);

                String encodedString = Base64.getEncoder().encodeToString(data);
                out.write(encodedString);
                break;
            }
            // Updated to receive a final capitalized phrase from the server
            if (fromServer.equals("BYE.")) /* exit statement */
                break;

            t = t1 - t0;

            // Captures the total transmission size of the message
            totalTransmissionSize += t;
            System.out.println("Cycle time: " + t);

            fromUser = fromFile.readLine(); // reading strings from a file
            if (fromUser != null) {
                System.out.println("Client: " + fromUser);
                out.println(fromUser); // sending the strings to the Server via ServerRouter

                msgCount++; // Incrementing message count
                totalMsgSize += fromUser.length(); // adding length of message to totalMsgSize
                t0 = System.currentTimeMillis();
            }
        }

        // closing connections
        out.close();
        in.close();
        Socket.close();

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
    }
}