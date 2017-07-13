package client;

import protocol.WeatherDataProtocol;
import rmi.WeatherDataRemote;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static protocol.WeatherDataProtocol.ERROR_DATE_PARSE;
import static protocol.WeatherDataProtocol.ERROR_NO_INFO_FOR_DATE;

public class Client implements Runnable {
    public Client() {
    }

    @Override
    public void run() {
        try {
            System.out.println("----------------------------------\n- Welcome in the weather service -\n----------------------------------");
            BufferedReader buReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Give me a date (YYYYMMDD):\t");
            String date = buReader.readLine();
            String response = null;
            //when socket is close input and ouput stream are closed aswell
            try {
                Registry registry = LocateRegistry.getRegistry(null);
                WeatherDataRemote stub = (WeatherDataRemote) registry.lookup(WeatherDataProtocol.REGISTRY_NAME);
                response = stub.getWeatherData(date);
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
                response = processResponse(response);
                System.out.println(response);
            }
         catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private String processResponse(String requestData) {
        if (requestData.equals(ERROR_DATE_PARSE)) {
            return "Passed date is invalid";
        }
        if (requestData.equals(ERROR_NO_INFO_FOR_DATE)) {
            return "No weather data for given date.";
        }

        String[] data = requestData.split(",");
        int minTemperature = Integer.valueOf(data[0]);
        int maxTemperature =Integer.valueOf(data[1]);
        double averageTemperature = Double.valueOf(data[2]);

        StringBuilder builder = new StringBuilder("Weather data for the given date\n");

        for (int i = 0; i < data.length-3; i++) {
            builder.append("\t" + (i < 10 ? "0" + i : i) + ":00\t" + data[i+3] + "\u00b0\n");
        }
        builder.append("\t\tMAX\t" + maxTemperature + "\u00b0\n");
        builder.append("\t\tMIN\t" + minTemperature + "\u00b0\n");
        builder.append("\t\tAVERAGE\t" + averageTemperature + "\u00b0\n");
        return builder.toString();
    }
}