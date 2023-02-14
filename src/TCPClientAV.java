import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.sound.sampled.*;

public class TCPClientAV {
    //Variables for calculations
    int msgCount = 0, totalMsgSize = 0, totalTransmissionSize = 0;
    private static BufferedInputStream inputStream;

    public TCPClientAV() throws IOException {
    }

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        // Variables for setting up connection and communication
        Socket Socket = null; // socket to connect with ServerRouter
        OutputStream out = null; // for writing to ServerRouter
        InputStream in = null; // for reading form ServerRouter
        Scanner sc = new Scanner(System.in);
        InetAddress addr = InetAddress.getLocalHost();
        String localHost = addr.getHostAddress(); // Client machine's IP
        String routerName = "10.0.0.66"; // ServerRouter localHost name
        int SockNum = 5555; // port number

        // Tries to connect to the ServerRouter
        try {
            Thread.sleep(3000);
            Socket = new Socket(routerName, SockNum);
            out = Socket.getOutputStream();
            in = Socket.getInputStream();
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about router: " + routerName);
            System.exit(1);
        }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + routerName);
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Variables for message passing
        inputStream = new BufferedInputStream(Socket.getInputStream());
        AudioInputStream ais = AudioSystem.getAudioInputStream(inputStream); // reader for the string file
        String fromServer; // messages received from ServerRouter
        String fromUser = null; // messages sent to ServerRouter
//			String address =routerName; // destination IP (Server)
        String address ="10.0.0.66"; // destination IP (Server)
        double t0, t1, t;
        //Variables for calculations
        int msgCount = 0, totalMsgSize = 0, totalTransmissionSize = 0;

        // Communication process (initial sends/receives
        out.write(address.getBytes());// initial send (IP of the destination Server)
        fromServer = sc.nextLine();//initial receive from router (verification of connection)
        System.out.println("ServerRouter: " + fromServer);
        out.write(localHost.getBytes()); // Client sends the IP of its machine as initial send
        t0 = System.currentTimeMillis();

        Clip clip = AudioSystem.getClip();
        clip.open(ais);
        clip.start();

        // Communication while loop
        while (inputStream != null) {
            t1 = System.currentTimeMillis();
            if (clip.isActive()) {

                System.out.println("********** Buffred *********" + inputStream.available());

            }
            t = t1 - t0;
            totalTransmissionSize += t;
            System.out.println("Cycle time: " + t);

            t0 = System.currentTimeMillis();

            }// reading strings from a file

        // closing connections
        out.close();
        in.close();
        Socket.close();
        clip.close();

        double avgMessageSize = 0, avgTransmissionTime = 0, avgLookUpTime = 0;
        if (msgCount > 0) {
            avgMessageSize = (double) totalMsgSize/msgCount; //Calculating average message size
            avgTransmissionTime = (double) totalTransmissionSize/msgCount;
        }

        //Writing statistical results to results.txt
        FileWriter resultFW = new FileWriter("results.txt");
        BufferedWriter resultBW = new BufferedWriter(resultFW);
        resultBW.write("Average message size is: " + avgMessageSize+" characters");
        resultBW.newLine();
        resultBW.write("Average transmission time is: "+(avgTransmissionTime/60)+" seconds");
        resultBW.close();
        resultFW.close();
    }
}
