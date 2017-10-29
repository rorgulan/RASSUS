
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import sensor.MeasurementServerSensor;
import sensor.Sensor;

/**
 *
 * @author renato
 * @version 1.0
 *
 *
 * Homework1 - Collecting and processing sensor data in IoT
 */

public class RunSensor {

    public static void main(String[] args) throws IOException {

        /* Client start "measure" and responsible for "clients"  , sensors */
        MeasurementServerSensor serverSensor = new MeasurementServerSensor();
        Thread thread;
        thread = new Thread(serverSensor);
        thread.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String command = reader.readLine();

        /* When client input RUN then sensor contact neighbour sensor */
        if (command.contains("RUN")) {
            Sensor clientSensor = new Sensor(serverSensor.getIdentificator());
            clientSensor.run();
        }

    }
}
