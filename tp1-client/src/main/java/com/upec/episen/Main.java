package com.upec.episen;

import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    protected static Logger logger = LogManager.getLogger(Main.class);

    protected static Properties properties = new Properties();

    static{
        logger.info("tp1-client is initializing...");
        // charger les propriétés de configuration
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");

        try {
            properties.load(is);
            logger.info("properties loaded successfully.");
        } catch (Exception e) {
            logger.error("Failed to load configuration properties.", e);
        }
    }

    public static void main(String[] args) {
        logger.warn("tp1-client started...");
        if (args.length != 1) {
            logger.error("Usage: java -jar tp1-client.jar <numPackets>");
            return;
        }
        final int numPackets = Integer.parseInt(args[0]);
        Main client = new Main();
        boolean running = client.start(numPackets);
        logger.warn("tp1-client finished with status: {}", running ? "success" : "failure");
    }

    protected InetAddress address;

        public Main() {
            try {
                this.address = InetAddress.getByName(properties.getProperty("server.host"));
                logger.info("Server address resolved {}", this.address);
            } catch (Exception e) {
                logger.error("Failed to resolve server address.", e);
            }
        }

        protected boolean start(final int numPackets) {
            logger.info("Starting tp1-client with {}", this.address);
            try (DatagramSocket socket = new DatagramSocket()) {
                for (int i = 0; i < numPackets; i++) {
                    String message = "Jeudi, 5 Février 2026";
                    final int PORT = Integer.parseInt(properties.getProperty("server.port"));
                    byte[] array = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(array, array.length, this.address, PORT);
                    socket.send(packet);
                    logger.info("Message sent to server {}", message);
                }
                return true;
            
            } catch (SocketException se) {
                logger.error("Socket error occurred while starting the client.", se);
                return false;
            
            } catch (Exception e) {
                logger.error("An error occurred while starting the client.", e);
                return false;
            }
        }
}