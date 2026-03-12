package fr.upec.espisen;

import org.apache.logging.log4j.Logger;

import fr.upec.espisen.service.Behaviour;
import fr.upec.espisen.service.ClientBehaviour;
import fr.upec.espisen.service.ServerRunning;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;

public class Main {
    public static final Logger logger = LogManager.getLogger(Main.class.getName());
    protected static Properties properties;
    // protected static ThreadGroup group;
    protected static ExecutorService executorService;
    protected static ConcurrentHashMap<UUID, Future<String>> clientStatusMap;
    protected static Boolean running;

    public static void main(String[] args) {
        logger.info("tp2-server started");

        // 1. Loading data from application properties file
        properties = new Properties();
        final int THREAD_POOL_SIZE = Integer.parseInt(System.getProperty("server.threadPoolSize", "10"));
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        clientStatusMap = new ConcurrentHashMap<>();
        running = true;
        registerBean();

        try (InputStream input = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("application.properties")) {
            properties.load(input);
            // group = new ThreadGroup("tp2-server");

            // 2. Creation of tcp server on port sever.port
            final int PORT = Integer.parseInt(properties.getProperty("server.port"));
            ServerSocket serverSocket = new ServerSocket(PORT);

            // 3. Waiting for clients to connect and creating a new thread for each client
            while (running) {
                Socket socket = serverSocket.accept();
                InputStream fromClient = socket.getInputStream();
                OutputStream toClient = socket.getOutputStream();
                // Behaviour behaviour = new Behaviour(fromClient, toClient);
                // Thread thread = new Thread(group, behaviour);
                // thread.start();
                Future<String> status = executorService.submit(new ClientBehaviour(fromClient, toClient));
                clientStatusMap.put(UUID.randomUUID(), status);
                logger.info("waiting for next client...");
            }
            serverSocket.close();
            logger.info("tp2-server stopped");
        } catch (IOException e) {
            logger.error("Error loading application properties", e);
        }

    }

    public static Boolean getRunning() {
        return running;
    }

    public static void setRunning(Boolean running) {
        Main.running = running;
        if (!running) {
            for (Map.Entry<UUID, Future<String>> entry : clientStatusMap.entrySet()) {
                try {
                    logger.info("status = %s", entry.getValue().get().toString());
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error getting client status", e);
                }
            }
        }
    }

    // 4. Registering the ServerRunning MBean
    public static void registerBean() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name;
        try {
            name = new ObjectName("fr.upec.espisen.server:type=ServerRunning");
            mbs.registerMBean(new ServerRunning(), name);
            logger.info("ServerRunning MBean registered successfully");
        } catch (Exception e) {
            logger.error("Error registering MBean", e);
        }
    }
}
