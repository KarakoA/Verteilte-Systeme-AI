package client;

import protocol.WeatherDataProtocol;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import static protocol.WeatherDataProtocol.ERROR_DATE_PARSE;
import static protocol.WeatherDataProtocol.ERROR_NO_INFO_FOR_DATE;

/**
 * Client class for the weather data program. Captures the input of the user and sends a request to the weather server and
 * then prints the info to the user.
 */
public class Client implements Runnable {
    @Override
    public void run() {
        try {
            System.out.println("----------------------------------\n- Welcome in the weather service -\n----------------------------------");
            BufferedReader buReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Give me a date (YYYYMMDD):\t");
            String date = buReader.readLine();
            InetAddress address = InetAddress.getLocalHost();
            //when socket is close input and ouput stream are closed aswell
            try (Socket socket = new Socket(address, WeatherDataProtocol.SERVER_PORT)) {
                PrintWriter writer = new PrintWriter(new DataOutputStream(socket.getOutputStream()), true);
                writer.println(date);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = processResponse(reader.readLine());
                System.out.println(response);
            }
        } catch (Throwable t) {
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
        int maxTemperature = Integer.valueOf(data[1]);
        double averageTemperature = Double.valueOf(data[2]);

        StringBuilder builder = new StringBuilder("Weather data for the given date\n");

        for (int i = 0; i < data.length - 3; i++) {
            builder.append("\t" + (i < 10 ? "0" + i : i) + ":00\t" + data[i + 3] + "\u00b0\n");
        }
        builder.append("\t\tMAX\t" + maxTemperature + "\u00b0\n");
        builder.append("\t\tMIN\t" + minTemperature + "\u00b0\n");
        builder.append("\t\tAVERAGE\t" + averageTemperature + "\u00b0\n");
        return builder.toString();
    }
}