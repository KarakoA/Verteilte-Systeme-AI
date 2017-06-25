package server;

import java.io.IOException;


public class ServerApplication {
    /**
     * Server application entry point.
     *
     * @param args - not used
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        Server s = new Server();
        new Thread(s).start();
        System.out.println("-------Server started-------");
    }
}
