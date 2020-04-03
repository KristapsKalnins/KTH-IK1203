import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HTTPEcho {
    static int buffer = 64 * 1024;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]), 0, InetAddress.getByName(null));
            while (true) {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(1000);
                InputStream receive = socket.getInputStream();
                OutputStream send = socket.getOutputStream();
                ArrayList<Byte> inbuff = new ArrayList<>();
                inbuff.ensureCapacity(buffer);

                int singleByte;
                String rec;
                int first = 0;
                int second = 0;
                int third= 0;

                try {
                    while ((singleByte = receive.read()) != -1) {
                        if (third == 13 && second == 10 && first == 13 && singleByte == 10) break;
                        inbuff.add((byte) singleByte);
                        third = second;
                        second = first;
                        first  = singleByte;
                    }
                } catch (SocketTimeoutException ignored) {

                }
                rec = decodeArray(inbuff);
                
                rec = rec.replaceAll("\r\n", "<br/>");

                String responseBody =  "<!doctype html>\r\n" +
                        "<html>\r\n<body>\r\n<p>"+ rec +"</p>\r\n</body>\r\n</html>";

                String response = "HTTP/1.1 200 OK\r\n" +
                                "Server: TestServer\r\n" +
                                "Content-Type: text/html\r\n"+
                                String.format("Content-Length: %d\r\n\r\n", responseBody.getBytes(StandardCharsets.UTF_8).length) +
                                responseBody;


                send.write(response.getBytes(StandardCharsets.UTF_8));


                receive.close();
                send.close();
                socket.close();

            }
        } catch (IOException e) {
            System.out.println("Error opening the socket!");

        }
    }





    public static String decodeArray(ArrayList<Byte> inbuff){
        inbuff.trimToSize();
        Byte[] wrapped = new Byte[inbuff.size()];
        inbuff.toArray(wrapped);

        byte[] finReadOut = new byte[wrapped.length];
        int i = 0;
        for (Byte b : wrapped)
            finReadOut[i++] = b;
        return new String(finReadOut, StandardCharsets.UTF_8);
    }
}




