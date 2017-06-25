package server;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import protocol.WeatherDataProtocol;
import rmi.WeatherDataRemote;

public class ServerApplication {
	/**
	 * Server application entry point.
	 *
	 * @param args
	 *            - not used
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		try {
			Server obj = new Server();
			WeatherDataRemote stub = (WeatherDataRemote) UnicastRemoteObject.exportObject(obj, 0);

			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.bind(WeatherDataProtocol.REGISTRY_NAME, stub);

			System.err.println("Server ready");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
