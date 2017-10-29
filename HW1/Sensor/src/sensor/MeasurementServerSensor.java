/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author renato
 * @version 1.0
 */

/*
 * This class represent client (sensor) as a TCP server for communication with
 * other TCP clients (sensors) , called multithread server
 */

public class MeasurementServerSensor extends Thread implements ServerIf, Runnable {

    private static String hostName = null;
    private ServerSocket serverSocket;
    private final static int BACKLOG = 10;                  // max number of client in buffer
    private static int PORT;                                // port of sensorTCPserver
    private static final int NUMBER_OF_THREADS = 4;         // max number of threads
    private static final String FILENAME = "/home/renato/Desktop/4.godina/RaspodjeljeniSustavi/DomacaZadaca/mjerenja.csv";
    private final AtomicBoolean runningFlag;
    private final ExecutorService executor;
    private final AtomicInteger activeConnections;
    private UUID identificator;
    private static final double MIN_LONGITUDE = 15.87;
    private static final double MAX_LONGITUDE = 16;
    private static final double MIN_LATITUDE = 45.75;
    private static final double MAX_LATTITUDE = 45.85;
    private double randomLatitude;
    private double randomLongitude;
    static long startTimeInSeconds;
    static ArrayList<String> measurementResult;
    static String[] headerOfCSV;

    public MeasurementServerSensor() {

        
        /* Generate Sensor Server PORT */
        PORT = -1;
        try (ServerSocket socket = new ServerSocket(0);) {
            PORT = socket.getLocalPort();
            socket.close();
        } catch (IOException exc) {
        }

        hostName = HostIP();
        if (getPort() < 1024) {
            System.err.println("Illgeal port number [" + PORT + "]");
            System.exit(-1);
        }

        activeConnections = new AtomicInteger(0); // new AtomicInteger with the
        // given initial value zero
        executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        runningFlag = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        startTimeInSeconds = TimeInSeconds();
        System.err.println(startTimeInSeconds);
        System.out.println("Reading measurement...");
        measurementResult = measurementCSV();
        System.out.println("Measurement data are stored");
        System.out.println("Generating sensor ID...");
        generateIdentificator();
        System.out.println(identificator.toString());
        System.out.println("Sensor search geographic location...");
        geographicLocation();
        System.out.println("Geographic location : LONGITUDE - " + randomLongitude + " " + "LATITUDE - " + randomLatitude);
        System.out.println("Registering to server...");
        System.out.println("Sensor local IP: " + hostName + " " + "PORT: " + PORT);
        System.out.println(register(identificator.toString(), randomLatitude, randomLongitude, hostName, PORT));
        sensorStartup();

    }

    public int getPort() {
        return PORT;
    }

    public UUID getIdentificator() {
        return this.identificator;
    }

    public String HostIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (java.net.UnknownHostException e) {
            return "localhost";
        }
    }

    /* This method starts all required sensorTCPserver services */
    @Override
    public void sensorStartup() {

        try (ServerSocket serverSocket = new ServerSocket(PORT, BACKLOG);) {
            this.serverSocket = serverSocket;
            serverSocket.setSoTimeout(500); // set timeout to avoid blocking
            System.out.println();
            System.err.println("Server info : " + PORT + " " + hostName);
            loop();
        } catch (IOException e) {
            System.err.println("Exception caught when opening or setting the socket: " + e);
        } finally {
            executor.shutdown();
        }
    }

    public void generateIdentificator() {
        this.identificator = UUID.randomUUID();
    }

    public void geographicLocation() {
        Random r = new Random();
        randomLongitude = MIN_LONGITUDE + (MAX_LONGITUDE - MIN_LONGITUDE) * r.nextDouble();
        randomLatitude = MIN_LATITUDE + (MAX_LATTITUDE - MIN_LATITUDE) * r.nextDouble();
    }

    private long TimeInSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /* This method is processing other sensors requests */
    @Override
    public void loop() {
        while (getRunningFlag()) {
            // Try to create a new socket, to be able listen connection made to
            // this socket
            System.out.println("Sensor Server wait for clients...");
            try {
                Socket clientSocket = serverSocket.accept(); // This represents
                // method accept
                // In a new thread execute a tcp request handler
                Runnable worker;
                worker = new SensorWorker(hostName, PORT, clientSocket, startTimeInSeconds, runningFlag,
                        activeConnections);
                executor.execute(worker);
                activeConnections.set(activeConnections.get() + 1);
            } catch (SocketTimeoutException e) {
                // nothing to do, check runningFlag
            } catch (IOException exc) {
                System.err.println("Exception caught when waiting for a connection: " + exc);
            }
        }
        runningFlag.set(false); // If exception didn't update flag
    }

    @Override
    public void shutdown() {
        while (activeConnections.get() > 0) {
            System.out.println("WARNING: There are still alive connections");
            try {
                Thread.sleep(5000);
            } catch (java.lang.InterruptedException e) {
            }
        }
        if (activeConnections.get() == 0) {
            System.out.println("Server shutdown.");
        }
    }

    @Override
    public boolean getRunningFlag() {
        return runningFlag.get();
    }

    @Override
    public void setRunningFlag(boolean flag) {
        this.runningFlag.set(flag);
    }

    /* Method for reading file measurement.csv */
    private ArrayList<String> measurementCSV() {
        ArrayList<String> data = new ArrayList<>();
        String lineOfFile;
        File measurementFile = new File(FILENAME);
        try (BufferedReader reader = new BufferedReader(new FileReader(measurementFile))) {
            String nameRow = reader.readLine();
            headerOfCSV = nameRow.split(",", -1);
            while ((lineOfFile = reader.readLine()) != null) {
                data.add(lineOfFile);
            }
        } catch (IOException e) {
        }
        return data;
    }

    private class SensorWorker implements Runnable {

        private Socket copySocket = null;
        private final AtomicBoolean flag;
        private final AtomicInteger connections;
        private final int port;
        private final String IPaddress;
        private long startTimeInSeconds;
        private long activeSeconds;

        public SensorWorker(String hostName, int port, Socket clientSocket, long StartTimeInSeconds, AtomicBoolean flag,
                AtomicInteger activeConnections) {
            this.IPaddress = hostName;
            this.port = port;
            this.copySocket = clientSocket;
            this.startTimeInSeconds = startTimeInSeconds;
            this.flag = flag;
            this.connections = activeConnections;
        }

        @Override
        public void run() {
            // Need to create BufferedReader for reading client input
            try (BufferedReader readerFromClient = new BufferedReader(
                    new InputStreamReader(copySocket.getInputStream()));
                    // create a PrintWriter from an existing OutputStream
                    PrintWriter outputForClient = new PrintWriter(new OutputStreamWriter(copySocket.getOutputStream()),
                            true);) {
                System.out.println("Sensor accept neighbour sensor.");
                String receivedString;

                // Sensor constantly measures and send measurement while connection is established
                while ((receivedString = readerFromClient.readLine()) != null) {
                    System.out.println("Sensor server listen on IP:" + this.IPaddress + " PORT:" + this.port);
                    System.out.println("Server received:" + receivedString);
                    // shutdown the server if requested
                    if (receivedString.contains("shutdown")) {
                        flag.set(false);
                        activeConnections.set(activeConnections.get() - 1);
                        return;
                    }
                    if (receivedString.contains("request")) {
                        activeSeconds = activeTimeInSeconds(startTimeInSeconds);
                        String measurementForClient = calculateMeasurementRow(activeSeconds);
                        // send a String then terminate the line and flush	
                        outputForClient.println(measurementForClient);
                        System.out.println("SensorServer sent: " + measurementForClient);
                    }
                }
                connections.set(connections.get() - 1);
            } catch (IOException exc) {
                System.err.print("Exception caught when trying to read or send data: " + exc);
            }
        }
    }

    private long activeTimeInSeconds(long startTimeInSeconds) {
        return TimeInSeconds() - startTimeInSeconds;
    }

    private String calculateMeasurementRow(long activeSeconds) {
        String measurement;
        int rowNumber = (int) ((activeSeconds % 100) + 2);
        measurement = measurementResult.get(rowNumber);
        System.out.println("Sensor measurement info: " + rowNumber + " " + activeSeconds + " " + measurement);
        return measurement;

    }

    private static boolean register(java.lang.String username, double latitude, double longitude, java.lang.String iPaddress, int port) {
        service.Service_Service service = new service.Service_Service();
        service.Service portt = service.getServicePort();
        return portt.register(username, latitude, longitude, iPaddress, port);
    }
}
