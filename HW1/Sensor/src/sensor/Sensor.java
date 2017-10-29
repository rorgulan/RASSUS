/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import service.UserAddress;

/**
 *
 * @author renato
 */
public class Sensor implements Runnable {

    /**
     * @param args the command line arguments
     */
    private String ID;
    private final int PORT;
    private final String IP;
    private UserAddress neighbour;
    private long activeTime;
    private long startTime;
    List<Integer> myMeasurement = new ArrayList<>();
    List<Integer> neighbourMeasurement = new ArrayList<>();
    float avg;
    boolean storeState;

    public Sensor(UUID sensorIdentificator) {
        this.ID = sensorIdentificator.toString();
        System.err.println("Sensor search neighbour...");
        this.neighbour = searchNeighbour(ID);
        this.IP = neighbour.getIPaddress();
        this.PORT = neighbour.getPort();
        this.startTime = MeasurementServerSensor.startTimeInSeconds;
        this.activeTime = activeTimeInSeconds(this.startTime);
    }

    @Override
    public void run() {
        if (neighbour == null) {
            System.err.println("No active sensor !");
            return;
        }
        System.out.println("Closest sensor with useraddress: " + neighbour.getIPaddress() + " " + neighbour.getPort());
        try (Socket neighbourSocket = new Socket(neighbour.getIPaddress(), neighbour.getPort());) {
            // Create reader for input stream of neighbour sensor 
            BufferedReader reader = new BufferedReader(new InputStreamReader(neighbourSocket.getInputStream()));
            // Create writer for output stream to neighbour sensor
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(neighbourSocket.getOutputStream()), true);
            System.out.println(startTime);
            System.out.println(activeTime);
            while (true) {
                Thread.sleep(2500);
                // sensor send request to neighbour sensor
                writer.println("request");
                String requesterMeasurement = calculateMeasurementRow(activeTime);
                // System.out.println("Sensor requester measurement: " + requesterMeasurement);
                String recievedMeasurement = reader.readLine();
                recievedMeasurement = recievedMeasurement.trim();
                if (recievedMeasurement == null) {
                    break;
                }
                System.out.println("Neighbour measurement: " + recievedMeasurement);
                myMeasurement = convertMeasureValueToInt(requesterMeasurement);
                neighbourMeasurement = convertMeasureValueToInt(recievedMeasurement);
                for (int i = 0; i < MeasurementServerSensor.headerOfCSV.length; i++) {
                    avg = AverageMeasurement(myMeasurement.get(i), neighbourMeasurement.get(i));
                    storeState = storeMeasurement(ID, MeasurementServerSensor.headerOfCSV[i], avg);
                    // System.out.println(storeState);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String calculateMeasurementRow(long activeSeconds) {
        String measurement;
        int rowNumber = (int) ((activeSeconds % 100) + 2);
        measurement = MeasurementServerSensor.measurementResult.get(rowNumber);
        System.out.println("Sensor measurement info: " + rowNumber + " " + activeSeconds + " " + measurement);
        return measurement;

    }

    private long activeTimeInSeconds(long startTimeInSec) {
        return TimeInSeconds() - startTimeInSec;
    }

    private long TimeInSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    private List<Integer> convertMeasureValueToInt(String measurement) {
        List<Integer> measurementList;
        measurementList = new ArrayList<>(6);
        String[] splittedMeasurement = measurement.split(",", -1);
        for (String param : splittedMeasurement) {
            measurementList.add(convertToInt(param));
        }
        return measurementList;
    }

    private int convertToInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private float AverageMeasurement(Integer param1, Integer param2) {
        if (param1 == null && param2 == null) {
            avg = 0;
            return avg;
        } else if (param2 == null) {
            avg = (float) param1;
            return param1;
        } else if (param1 == null) {
            avg = (float) param2;
            return param2;
        } else {
            avg = ((float) param1 + (float) param2) / 2.0f;
        }
        return avg;
    }

    private static UserAddress searchNeighbour(java.lang.String username) {
        service.Service_Service service = new service.Service_Service();
        service.Service port = service.getServicePort();
        return port.searchNeighbour(username);
    }

    private static boolean storeMeasurement(java.lang.String username, java.lang.String parametar, float averageValue) {
        service.Service_Service service = new service.Service_Service();
        service.Service port = service.getServicePort();
        return port.storeMeasurement(username, parametar, averageValue);
    }

}
