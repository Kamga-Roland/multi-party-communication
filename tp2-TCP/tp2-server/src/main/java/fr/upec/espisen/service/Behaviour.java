package fr.upec.espisen.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.crypto.Data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Behaviour implements Runnable {

    protected static Logger logger = LogManager.getLogger(Behaviour.class);

    protected InputStream fromClient;
    protected OutputStream toClient;

    public Behaviour(InputStream fromClient, OutputStream toClient) {
        this.fromClient = fromClient;
        this.toClient = toClient;
    }

    @Override
    public void run() {
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
            logger.info("Sending response: {}", response);
            dos.writeUTF(response);
            // dos.flush();
        } catch (Exception e) {
            logger.error("Error reading from client", e);
        }
    }

}
