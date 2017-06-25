package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WeatherDataRemote extends Remote{
	
	String getWeatherData(String date) throws RemoteException;

}
