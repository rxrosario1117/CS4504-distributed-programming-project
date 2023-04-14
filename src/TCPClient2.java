import java.io.*;
import java.net.*;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TCPClient2 {
    public static void main(String[] args) throws IOException {

        // Variables for setting up connection and communication
        Socket serverRouterSocket = null; // socket to connect with ServerRouter
        Socket clientCommSocket = null; // socket to connect clients
        PrintWriter out = null; // for writing to ServerRouter
        BufferedReader in = null; // for reading form ServerRouter
        PrintWriter clientOut = null; // for writing to ServerRouter
        BufferedReader clientIn = null; // for reading form ServerRouter
        InetAddress addr = InetAddress.getLocalHost();
        String localHost = addr.getHostAddress(); // Client machine's IP
        // name of client to be put in RTable
        String userName = "T2";

        // IP for my local server router machine
        String routerName = "192.168.1.71"; // ServerRouter localHost name

        int SockNum = 5556; // port number

        // Tries to connect to the ServerRouter
        try {
//            Socket = new Socket(routerName, SockNum);
            serverRouterSocket = new Socket("localhost", SockNum);
            out = new PrintWriter(serverRouterSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(serverRouterSocket.getInputStream()));
            out.println("Hello from client 2 AKA " + userName);
            out.println(userName);
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
        String fromRouter; // messages received from ServerRouter
        String fromUser; // messages sent to ServerRouter
        String address ="192.168.1.71";// Local IP for the server
        String destinationName = "T1"; // destination Name (Client 2)
        String SenderNickname = "T2";

        long t0, t1, t;

        // Variables for calculations and gathering data
        int msgCount = 0, totalMsgSize = 0, totalTransmissionSize = 0;

        // Communication process (initial sends/receives)
        out.println(destinationName);// initial send (IP of the destination Server)
        fromRouter = in.readLine();// initial receive from router (verification of connection)

        // Get the port num for client comms
        int portNum = Integer.parseInt(in.readLine());

//        Set up socket for clients to communicate
        try {
            ServerSocket clientSocket = new ServerSocket(portNum);
            clientCommSocket = clientSocket.accept();

        }catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // Reader/Writer for the clientCommSocket
        clientIn = new BufferedReader(new InputStreamReader(clientCommSocket.getInputStream()));
        clientOut = new PrintWriter(clientCommSocket.getOutputStream(), true);

        System.out.println("ServerRouter: " + fromRouter);
        out.println(SenderNickname);
        out.println(localHost); // Client sends the IP of its machine as initial send


        out.println(fileName);
        t0 = System.currentTimeMillis();

        // Temp loop to stop client from terminating
//        while (true) {
//
//        }

//        // Communication while loop
//        while ((fromRouter = in.readLine()) != null) {
//            System.out.println("Server: " + fromRouter);
//            t1 = System.currentTimeMillis();
//            if (!fileName.contains(".txt")) {
//                Path path = Paths.get(fileName);
//                byte[] data = Files.readAllBytes(path);
//
//                String encodedString = Base64.getEncoder().encodeToString(data);
//                out.write(encodedString);
//                break;
//            }
//            // Updated to receive a final capitalized phrase from the server
//            if (fromRouter.equals("BYE.")) /* exit statement */
//                break;
//
//            t = t1 - t0;
//
//            // Captures the total transmission size of the message
//            totalTransmissionSize += t;
//            System.out.println("Cycle time: " + t);
//
//            fromUser = fromFile.readLine(); // reading strings from a file
//            if (fromUser != null) {
//                System.out.println("Client: " + fromUser);
//                out.println(fromUser); // sending the strings to the Server via ServerRouter
//
//                msgCount++; // Incrementing message count
//                totalMsgSize += fromUser.length(); // adding length of message to totalMsgSize
//                t0 = System.currentTimeMillis();
//            }
//        }
//
//        // closing connections
//        out.close();
//        in.close();
//        Socket.close();
//
//        // Stores the metrics gathered and performs some final calculations
//        double avgMessageSize = 0, avgTransmissionTime = 0;
//        if (msgCount > 0) {
//            avgMessageSize = (double) totalMsgSize / msgCount; // Calculating average message size
//            avgTransmissionTime = (double) totalTransmissionSize / msgCount;
//        }
//
//        // Writing statistical results to results.txt
//        try {
//            FileWriter resultFW = new FileWriter("results.txt");
//            BufferedWriter resultBW = new BufferedWriter(resultFW);
//            resultBW.write("Average message size is: " + avgMessageSize);
//            resultBW.newLine();
//            resultBW.write("Average transmission time is: " + avgTransmissionTime);
//            resultBW.close();
//        } catch (IOException e) {
//            System.out.println("Error writing to file: " + e.getMessage());
//        }

        String fromClient, fromServer;

        clientOut.println("hello");

    // Rx while loop
        // Communication while loop
        String textType;
        int index = 0;
        String tempString = null;

        while ((fromClient = clientIn.readLine()) != null) {

            System.out.println("Client said: " + fromClient);
            if(index == 0){
                tempString = fromClient;
                clientOut.println("CONNECTED");
            }
//            if(tempString.contains(".txt")){
            if(false){
                System.out.println("HERE");
                if (fromClient.equals("Bye.")) { // exit statement
                    clientOut.println(fromClient.toUpperCase());
                    break;
                }

                fromServer = fromClient.toUpperCase(); // converting received message to upper case
                System.out.println("Server said: " + fromServer);
                clientOut.println(fromServer); // sending the converted message back to the Client via ServerRouter
            }
//            else if (index != 0){
            else if (true){

                clientOut.println("NOT A TEXT FILE");

//                int fileSize = Integer.parseInt(fromClient);
//                System.out.println("File size: " + fileSize);

                String encodedData = clientIn.readLine();

                // Timer stops

                byte[] decodedData = Base64.getDecoder().decode(encodedData);

//                Path filePath = Paths.get("./NEWCantinaBand3.wav");
//                Path filePath = Paths.get("./NEWtext.txt");
                Path filePath = Paths.get("./NEWvideo.mp4");
                Files.write(filePath,decodedData, StandardOpenOption.CREATE_NEW);
                break;
            }
            index++;
        }

        // closing connections
        clientIn.close();
        clientOut.close();

    }
}