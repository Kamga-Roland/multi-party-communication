package fr.upec.espisen;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    protected static Logger logger = LogManager.getLogger(Main.class.getName());
    protected static Properties properties;

    public static void main(String[] args) {
        logger.info("tp2-client is started");
        // 0. Load properties from application.properties file
        properties = new Properties();
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream("application.properties")) { // Thread.currentThread().getContextClassLoader() << SAME AS >> Main.class.getClassLoader()
            properties.load(is);
            // 1. Connection to TCP server
            final int PORT = Integer.parseInt(properties.getProperty("server.port"));
            final String HOST = properties.getProperty("server.host");
            Socket socket = new Socket(HOST, PORT);

            // 2. Send a request to the server
            final String request = "Hello, Server! This is tp2-client.";
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(request);
            logger.info("Request sent to server: {}", request);

            // 3. Receive the response from the server
            InputStream isr = socket.getInputStream();
            DataInput dis = new DataInputStream(isr);
            String response = dis.readUTF();
            logger.info("Response received from server: {}", response);
            
            // 4. Close the connection
            socket.close();
        } catch (Exception e) {
            logger.error("Error loading properties: {}", e.getMessage());
        } finally {
            logger.info("tp2-client is stopped");
        }
    }
}