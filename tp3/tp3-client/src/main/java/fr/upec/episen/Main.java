package fr.upec.episen;

import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    protected static Logger logger = LogManager.getLogger(Main.class);
    protected static Properties properties = new Properties();

    public static void main(String[] args) {
        logger.info("tp3-client started...");

        // Multicast client
        properties = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
        try {
            properties.load(is);
            final int PORT = Integer.parseInt(properties.getProperty("udp.server.port"));
            MulticastSocket socket = new MulticastSocket(PORT);
            logger.info("tp3-client is listening for multicast messages on port {}", PORT);
            InetAddress group = InetAddress.getByName(properties.getProperty("udp.server.address"));
            SocketAddress groupAddress = new InetSocketAddress(group, PORT);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(group);
            socket.joinGroup(groupAddress, networkInterface);
            logger.info("tp3-client joined multicast group: {}", group.getHostAddress());
            final int SIZE = Integer.parseInt(properties.getProperty("udp.packet.size"));
            DatagramPacket packet = new DatagramPacket(new byte[SIZE], SIZE);
            final int nbMsg = 3;
            int count = 0;
            while (count < nbMsg) {
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                logger.info("tp3-client received multicast message: {}", message);
                count++;
            }
            socket.leaveGroup(groupAddress, networkInterface);
            socket.close();
            logger.info("tp3-client left multicast group and closed socket");

        } catch (Exception e) {
            logger.error("Error loading properties: {}", e.getMessage());
        }
    }
}

