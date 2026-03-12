package fr.upec.espisen.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientBehaviour implements Callable<String> {

    protected static Logger logger = LogManager.getLogger(ClientBehaviour.class);

    protected InputStream fromClient;
    protected OutputStream toClient;

    public ClientBehaviour(InputStream fromClient, OutputStream toClient) {
        this.fromClient = fromClient;
        this.toClient = toClient;
    }
	
	@Override
	public String call() throws Exception {
        logger.info("Thread {} started", Thread.currentThread().getName());
        
        // Request processing
        DataInputStream dis = new DataInputStream(fromClient);
        DataOutputStream dos = new DataOutputStream(toClient);

        try {
            String request = dis.readUTF();
            logger.info("Received request: {}", request);

            // Response processing
            StringBuilder builder = new StringBuilder(request);
            String response = builder.reverse().toString();
            dos.writeUTF(response);
            logger.info("Sending response: {}", response);
            return "Success";
        } catch (Exception e) {
            logger.error("Error reading from client", e);
            return "Error";
        }

	}
}
