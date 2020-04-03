import java.io.*;
import java.net.*;
import java.net.ServerSocket;

public class ConcHTTPAsk {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]), 0, InetAddress.getByName(null));
            while (true) {
                Socket socket = serverSocket.accept();
                Thread session = new Thread(new HTTPAsk(socket));
                session.start();
            }
        } catch (IOException e) {
            System.err.println(e);

        }
    }
}
