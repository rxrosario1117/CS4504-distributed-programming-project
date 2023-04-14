import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

public class TCPServer {
    public static void main(String[] args) throws IOException {

        // Variables for setting up connection and communication
        Socket Socket = null; // socket to connect with ServerRouter
        PrintWriter out = null; // for writing to ServerRouter
        BufferedReader in = null; // for reading form ServerRouter
        InetAddress addr = InetAddress.getLocalHost();
        String host = addr.getHostAddress(); // Server machine's IP


        // This IP is the server router for my local network
        String routerName = "192.168.68.120"; // ServerRouter host name

        int SockNum = 5555; // port number

        // Tries to connect to the ServerRouter
        try {
//            Socket = new Socket(routerName, SockNum);
            Socket = new Socket("localhost", 5555);
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

        // Variables for message passing
        String fromServer; // messages sent to ServerRouter
        String fromClient; // messages received from ServerRouter

        // Client 1
// 			String address ="10.74.24.131"; // destination IP (Client 1)
        //Client2
        String address ="192.168.68.120"; // destination IP (Client 2)

        // Communication process (initial sends/receives)
        out.println(address);// initial send (IP of the destination Client)
        fromClient = in.readLine();// initial receive from router (verification of connection)
        System.out.println("ServerRouter: " + fromClient);
        fromClient = in.readLine();

        System.out.println(fromClient);
        // Communication while loop
        String textType;
        int index = 0;
        String tempString = null;
        while ((fromClient = in.readLine()) != null) {
            System.out.println("Client said: " + fromClient);
            if(index == 0){
                tempString = fromClient;
                out.println("CONNECTED");
            }
            if(tempString.contains(".txt")){
                System.out.println("HERE");
                if (fromClient.equals("Bye.")) { // exit statement
                    out.println(fromClient.toUpperCase());
                    break;
                }

                fromServer = fromClient.toUpperCase(); // converting received message to upper case
                System.out.println("Server said: " + fromServer);
                out.println(fromServer); // sending the converted message back to the Client via ServerRouter
            }
            else if (index != 0){
                out.println("NOT A TEXT FILE");
                String encodedData = fromClient;
                byte [] decodedData = Base64.getDecoder().decode(encodedData);
                Path filePath = Paths.get("NEWCantinaBand3.wav");
                Files.write(filePath,decodedData, StandardOpenOption.CREATE_NEW);
                break;
            }
            index++;
        }

        // closing connections
        out.close();
        in.close();
        Socket.close();
    }
}