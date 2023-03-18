import java.io.*;
import java.net.*;

public class TCPClient {

    public static void main(String[] args) throws IOException {
		// Variables for setting up connection and communication
        Socket Socket = null; // socket to connect with ServerRouter
        PrintWriter out = null; // for writing to ServerRouter
        BufferedReader in = null; // for reading form ServerRouter
        InetAddress addr = InetAddress.getLocalHost();
        String localHost = addr.getHostAddress(); // Client machine's IP

        // IP for my local server router machine
        String routerName = "192.168.1.182"; // ServerRouter localHost name

		int SockNum = 5555; // port number

		// Tries to connect to the ServerRouter
        try {
            Socket = new Socket(routerName, SockNum);
            out = new PrintWriter(Socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about router: " + routerName);
            System.exit(1);
        } 
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + routerName);
            System.exit(1);
        }

      	//FileIO streams and file
        File file = new File("./CantinaBand3.wav");
        DataInputStream dis = new DataInputStream(Socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(Socket.getOutputStream());
        FileInputStream fis = new FileInputStream(file);          
        
        // Variables for message passing	
        Reader reader = new FileReader("./file.txt");
        BufferedReader fromFile =  new BufferedReader(reader); // reader for the string file
        String fromServer; // messages received from ServerRouter
        String fromUser; // messages sent to ServerRouter

        BufferedInputStream inputStream = new BufferedInputStream(Socket.getInputStream());

        // Local IP for the server
        String address ="192.168.1.182"; // destination IP (Server)

        long t0, t1, t;

        //Variables for calculations and gathering data
        int msgCount = 0, totalMsgSize = 0, totalTransmissionSize = 0;
                
        // Communication process (initial sends/receives)
        out.println(address);// initial send (IP of the destination Server)
        fromServer = in.readLine();//initial receive from router (verification of connection)
        System.out.println("ServerRouter: " + fromServer);
        out.println(localHost); // Client sends the IP of its machine as initial send
        t0 = System.currentTimeMillis();
      	
        // Communication while loop
        while ((fromServer = in.readLine()) != null) {
            System.out.println("Server: " + fromServer);
            FileOutputStream fos = new FileOutputStream("recievedFile.wav");
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = dis.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, bytesRead);
                fos.flush();
            }
            fos.close();
            System.out.println("File recieved");
            t1 = System.currentTimeMillis();

            // Updated to receive a final capitalized phrase from the server
            if (fromServer.equals("BYE.")) /* exit statement */
                break;

            t = t1 - t0;

            //Captures the total transmission size of the message
            totalTransmissionSize += t;
            System.out.println("Cycle time: " + t);

            fromUser = fromFile.readLine(); // reading strings from a file
            if (fromUser != null) {
                System.out.println("Client: " + fromUser);
                out.println(fromUser); // sending the strings to the Server via ServerRouter

                msgCount++; // Incrementing message count
                totalMsgSize += fromUser.length(); //adding length of message to totalMsgSize
                t0 = System.currentTimeMillis();
            }

            byte[] sendBuffer = new byte[(int) file.length()];
            fis.read(sendBuffer);
            fis.close();
            dos.write(buffer, 0, sendBuffer.length);
            dos.flush();
            dos.close();
            System.out.println("File sent to ServerRouter");
        }

        // closing connections
        out.close();
        in.close();
        Socket.close();

        // Stores the metrics gathered and performs some final calculations
        double avgMessageSize = 0, avgTransmissionTime = 0;
        if (msgCount > 0) {
            avgMessageSize = (double) totalMsgSize/msgCount; //Calculating average message size
            avgTransmissionTime = (double) totalTransmissionSize/msgCount;
        }

        //Writing statistical results to results.txt
        try {
            FileWriter resultFW = new FileWriter("results.txt");
            BufferedWriter resultBW = new BufferedWriter(resultFW);
            resultBW.write("Average message size is: " + avgMessageSize);
            resultBW.newLine();
            resultBW.write("Average transmission time is: "+avgTransmissionTime);
            resultBW.close();
        } 
        catch (IOException e) {
            System.out.println("Error writing to file: "+e.getMessage());
        }
    }
}