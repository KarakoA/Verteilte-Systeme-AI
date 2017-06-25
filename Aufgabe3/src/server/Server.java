package server;

import protocol.WeatherDataProtocol;
import rmi.WeatherDataRemote;
import util.DateCSVReader;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Map;

public class Server implements WeatherDataRemote {

	/**
	 * Value format: Min,Max,Average,24h values
	 */
	private final Map<LocalDate, String> localDateToTimesMap;

	public Server() throws IOException {
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
	public String getWeatherData(String request) throws RemoteException {

		try {
			// format it
			LocalDate date = WeatherDataProtocol.processRequest(request);
			// check if we an entry for the date
			String response = localDateToTimesMap.get(date);

			if (response == null)
				return WeatherDataProtocol.ERROR_NO_INFO_FOR_DATE;
			return response;
		} catch (DateTimeException e) {
			return WeatherDataProtocol.ERROR_DATE_PARSE;
		}
	}

}
