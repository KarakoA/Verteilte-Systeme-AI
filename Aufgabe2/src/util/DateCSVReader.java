package util;

import protocol.WeatherDataProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


public class DateCSVReader {
    /**
     * Reads the weather data from a file and stores it in a map.
     */
    public Map<LocalDate, String> readFile(Path path) throws IOException {
        Map<LocalDate, String> localDateToTimesMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(path)))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] splitLine = line.split(",", 2);

                LocalDate date = LocalDate.parse(splitLine[0], WeatherDataProtocol.SERVER_DATE_FORMAT);
                String values = splitLine[1];
                localDateToTimesMap.put(date, values);
            }

            return localDateToTimesMap;
        }

    }
}