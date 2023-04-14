import javax.sound.sampled.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient2 {

    public static void main(String[] args) throws IOException, LineUnavailableException {

        // Variables for setting up connection and communication
        Socket Socket = null; // socket to connect with ServerRouter
        //PrintWriter out = null; // for writing to ServerRouter
        OutputStream out = null;
        BufferedReader in = null; // for reading from ServerRouter
        InetAddress addr = InetAddress.getLocalHost();
        String host = addr.getHostAddress(); // Client machine's IP
        System.out.println("Client Host: " + host);
//      	String routerName = "j263-08.cse1.spsu.edu"; // ServerRouter host name
        String routerName = "localhost"; // ServerRouter host name needs to be computer local ip address or "localhost"
        int SockNum = 5555; // port number

        // Tries to connect to the ServerRouter
        try {
            Socket = new Socket(routerName, SockNum, addr, 49999); // needs to be updated to constructor that sets local address and local port
//            out = new PrintWriter(Socket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
            in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
            out = Socket.getOutputStream();
            while (true) {
                AudioInputStream audio = testPlay("Yoda.wav");
                if (audio != null) {
                    AudioSystem.write(audio,AudioFileFormat.Type.WAVE , out);
                }
            }

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
        /*Reader reader = new FileReader("file.txt");
        BufferedReader fromFile =  new BufferedReader(reader); // reader for the string file
        String fromServer; // messages received from ServerRouter
        String fromUser; // messages sent to ServerRouter
//			String address ="10.5.2.109"; // destination IP (Server)
        String address ="192.168.1.69:49998"; // destination IP (Server)  /// DEBUGGED AND LOOKED AT THE DESTINATION in line 49 in sthread
        // BUT, this doesn't work because the IP Address is the same for all
        // this one needs to be the IP of the device running the server it is looking for or 127.0.0.1
        // needs to be ip address:port number in 40000 and computer ip address needs to be checked to make sure it hasn't changed
        //don't hardcode port numbers for anything not local
        long t0, t1, t;

        // Communication process (initial sends/receives
        /*out.println(address);// initial send (IP of the destination Server)
        fromServer = in.readLine();//initial receive from router (verification of connection)
        System.out.println("ServerRouter: " + fromServer);

        out.println(host + " From TCP Client"); // Client sends the IP of its machine as initial send
        t0 = System.currentTimeMillis();

        // Communication while loop
        while ((fromServer = in.readLine()) != null) {
            System.out.println("Server: " + fromServer);
            t1 = System.currentTimeMillis();
            if (fromServer.equals("Bye.")) // exit statement
                break;
            t = t1 - t0;
            System.out.println("Cycle time: " + t);

            fromUser = fromFile.readLine(); // reading strings from a file
            if (fromUser != null) {
                System.out.println("Client: " + fromUser);
                out.println(fromUser); // sending the strings to the Server via ServerRouter
                t0 = System.currentTimeMillis();
            }
        }*/

        // closing connections
        out.close();
        in.close();
        Socket.close();
    }
    public static AudioInputStream testPlay(String filename) {
        AudioInputStream din = null;
        try {
            File file = new File(filename);
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            System.out.println("Before :: " + in.available());

            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat =
                    new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, baseFormat.getSampleRate(),
                            8, baseFormat.getChannels(), baseFormat.getChannels(),
                            baseFormat.getSampleRate(), false);
            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            System.out.println("After :: " + din.available());
            return din;
        } catch (Exception e) {
            // Handle exception.
            e.printStackTrace();
        }
        return din;
    }
}
