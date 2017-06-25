package server;

import protocol.WeatherDataProtocol;
import util.DateCSVReader;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Server implements Runnable, AutoCloseable {

    private final ServerSocket host;
    private ExecutorService threadPool;
    private Logger logger;
    /**
     * Value format: Min,Max,Average,24h values
     */
    private final Map<LocalDate, String> localDateToTimesMap;

    public Server() throws IOException {
        host = new ServerSocket(WeatherDataProtocol.SERVER_PORT);
        final Path path = Paths.get("resources/data.csv");
        localDateToTimesMap = new DateCSVReader().readFile(path);
        threadPool=Executors.newCachedThreadPool();

        initLogger();
        computeMinMaxAverage();
    }
    private void initLogger() throws IOException{
        logger=Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        FileHandler fh= new FileHandler("resources/log.txt");
        fh.setFormatter(new SimpleFormatter());
        logger.addHandler(fh);
    }

    private void computeMinMaxAverage() {
        for (LocalDate key : localDateToTimesMap.keySet()) {
            String value = localDateToTimesMap.get(key);
            String[] data = value.split(",");

            int maxTemperature;
            int sumTemperature = 0;
            int minTemperature = maxTemperature = Integer.valueOf(data[0]);

            for (String temperature : data) {
                int temperatureData = Integer.valueOf(temperature);
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
                Socket clientConnection = host.accept();
                threadPool.submit(new ConnectionHandler(clientConnection));
            } catch (IOException e) {
                //if something goes wrong just stop
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            this.host.close();
        }finally {
            threadPool.shutdown();
        }
    }

    private class ConnectionHandler implements Runnable {
        Socket clientConnection;

        public ConnectionHandler(Socket clientConnection) {
            this.clientConnection = clientConnection;
        }

        @Override
        public void run() {
            //PID-should be the same for all threads, they are all part of the same process
            String pid=ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            //java thread id
            long threadID=Thread.currentThread().getId();
            String message= String.format("Address: %s:%d PID: %s ThreadID: %d",clientConnection.getInetAddress(),clientConnection.getPort(),pid,threadID);
            logger.log(Level.INFO,message);

            try (Socket clientConnection = this.clientConnection) {
                System.out.println("Processing");
                //delay for demonstration purposes
                Thread.sleep(5000);

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()))) {
                    String request = reader.readLine();
                    String response;
                    try {
                        //format it
                        LocalDate date = WeatherDataProtocol.processRequest(request);
                        //check if we have an entry for the date
                        response = localDateToTimesMap.get(date);
                        if (response == null)
                            response = WeatherDataProtocol.ERROR_NO_INFO_FOR_DATE;
                    } catch (DateTimeException e) {
                        response = WeatherDataProtocol.ERROR_DATE_PARSE;
                    }
                    //send it to the client
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientConnection.getOutputStream()))) {
                        writer.write(response);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            System.out.println("Done");
        }

    }
}
