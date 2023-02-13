import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FilePacketizer {
    private static final int PACKET_SIZE = 1024;

    public static byte[][] packetizeFile(String fileName) {
        byte[][] packets = null;
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            // Determine the number of packets
            long fileSize = fileInputStream.available();
            int numPackets = (int) Math.ceil((double) fileSize / PACKET_SIZE);
            packets = new byte[numPackets][];

            // Read the file into packets
            int bytesRead;
            byte[] packet = new byte[PACKET_SIZE];
            for (int i = 0; i < numPackets; i++) {
                bytesRead = fileInputStream.read(packet);
                packets[i] = new byte[bytesRead];
                System.arraycopy(packet, 0, packets[i], 0, bytesRead);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return packets;
    }

    public static byte[] reassemblePackets(InputStream inputStream) throws IOException {
        List<byte[]> packets = new ArrayList<>();
        int packetSize = 0;

        // Read packets until the end of the stream
        while (true) {
            byte[] packet = new byte[PACKET_SIZE];
            int bytesRead = inputStream.read(packet);

            if (bytesRead == -1) {
                break;
            }

            packets.add(packet);
            packetSize += bytesRead;
        }

        // Concatenate packets into a single file
        byte[] file = new byte[packetSize];
        int offset = 0;

        for (byte[] packet : packets) {
            System.arraycopy(packet, 0, file, offset, packet.length);
            offset += packet.length;
        }

        return file;
    }
}
