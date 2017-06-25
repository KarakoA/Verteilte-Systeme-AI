package server;

import protocol.WeatherDataProtocol;
import util.DateCSVReader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Map;

public class Server implements Runnable, AutoCloseable {

    private final ServerSocket host;
    /**
     * Value format: Min,Max,Average,24h values
     */
    private final Map<LocalDate, String> localDateToTimesMap;

    public Server() throws IOException {
        host = new ServerSocket(WeatherDataProtocol.SERVER_PORT);
        final Path path = Paths.get("resources/data.csv");
        localDateToTimesMap = new DateCSVReader().readFile(path);
        computeMinMaxAverage();
        System.out.println(localDateToTimesMap);
    }

    private void computeMinMaxAverage() {
        for (LocalDate key : localDateToTimesMap.keySet()) {
            String value = localDateToTimesMap.get(key);
            String[] data = value.split(",");

            int maxTemperature;
            int sumTemperature = 0;
            int minTemperature = maxTemperature = Integer.valueOf(data[0]);

            for (int i = 0; i < data.length; i++) {
                int temperatureData = Integer.valueOf(data[i]);
                sumTemperature += temperatureData;
                if (temperatureData < minTemperature)
                    minTemperature = temperatureData;
                if (temperatureData > maxTemperature)
                    maxTemperature = temperatureData;
            }
            double average = sumTemperature / 24.;
            value = String.format("%d,%d,%.2f,%s", minTemperature, maxTemperature, average, value);
            localDateToTimesMap.put(key, value);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                //block and wait for an incoming connection
                Socket s = host.accept();
                //no other requests can be accepted while the current one is processed.
                //Shouldn't be a problem since processing is quick.
                handleRequest(s);
            } catch (IOException e) {
                //if something goes wrong just stop
                e.printStackTrace();
                return;
            }
        }
    }


    private void handleRequest(Socket s) throws IOException {
        try (Socket socket = s) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String request = reader.readLine();
                String response;
                try {
                    //format it
                    LocalDate date = WeatherDataProtocol.processRequest(request);
                    //check if we an entry for the date
                    response = localDateToTimesMap.get(date);
                    if (response == null)
                        response = WeatherDataProtocol.ERROR_NO_INFO_FOR_DATE;
                } catch (DateTimeException e) {
                    response = WeatherDataProtocol.ERROR_DATE_PARSE;
                }
                //send it to the client
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                    writer.write(response);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public void close() throws IOException {
        this.host.close();
    }
}
