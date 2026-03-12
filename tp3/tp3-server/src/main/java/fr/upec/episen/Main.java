package fr.upec.episen;

import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    protected static Logger logger = LogManager.getLogger(Main.class);
    protected static Properties properties = new Properties();

    public static void main(String[] args) {
        logger.info("tp3-server started...");

        // Multicast server
        properties = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
        try {
            properties.load(is);

            try (MulticastSocket socket = new MulticastSocket()) {
                InetAddress group = InetAddress.getByName(properties.getProperty("udp.server.address"));
                final int PORT = Integer.parseInt(properties.getProperty("udp.server.port"));
                final int SIZE = Integer.parseInt(properties.getProperty("udp.packet.size"));
                final int nbMsg = 10;
                int count = 0;

                while (count < nbMsg) {
                    String message = "Message from multicast server!";
                    byte[] buffer = message.getBytes();
                    if (buffer.length > SIZE) {
                        logger.warn("Message size exceeds packet size, truncating message");
                        throw new IllegalArgumentException("Message size exceeds packet size");
                    }
                    DatagramPacket dp = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(dp);
                    logger.info("tp3-server sent multicast message: {}", message);
                    count++;
                    Thread.sleep(1000); // Sleep for 1 second before sending the next message   
                }

            } catch (Exception e) {
                logger.error("Error occurred while starting the server", e);
            }

        } catch (Exception e) {
            logger.error("Error loading properties: {}", e.getMessage());
        }
    }
}