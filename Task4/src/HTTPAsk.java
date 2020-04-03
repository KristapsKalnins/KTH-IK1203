import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HTTPAsk implements Runnable{
    static int buffer = 64 * 1024;
    Socket socket;

    public HTTPAsk(Socket socket){
    this.socket =  socket;
    }

    public void run(){
        try {
            socket.setSoTimeout(1000);
            InputStream receive = socket.getInputStream();
            OutputStream send = socket.getOutputStream();
            ArrayList<Byte> inbuff = new ArrayList<>();
            inbuff.ensureCapacity(buffer);

            int singleByte;
            int first = 0;
            int second = 0;
            int third = 0;

            try {
                while ((singleByte = receive.read()) != -1) {
                    if (third == 13 && second == 10 && first == 13 && singleByte == 10) break;
                    inbuff.add((byte) singleByte);
                    third = second;
                    second = first;
                    first = singleByte;
                }
            } catch (SocketTimeoutException ignored) {

            }

            String[] rec = decodeArray(inbuff).split(" ");

            if (rec[0].equals("GET") && rec[1].contains("/ask?") && rec[1].contains("hostname=") && rec[1].contains("port=")) {


                String host = null;
                String port = null;
                String string;

                String[] params = rec[1].split("\\?")[1].split("&");


                try {
                    if (params[0].startsWith("host")) {
                        host = params[0].split("=")[1];
                        if (params[1].startsWith("port")) {
                            port = params[1].split("=")[1];
                            string = params[2].split("=")[1];
                        } else {
                            port = params[2].split("=")[1];
                            string = params[1].split("=")[1];
                        }

                    } else if (params[0].startsWith("port")) {
                        port = params[0].split("=")[1];
                        if (params[1].startsWith("host")) {
                            host = params[1].split("=")[1];
                            string = params[2].split("=")[1];
                        } else {
                            host = params[2].split("=")[1];
                            string = params[1].split("=")[1];
                        }
                    } else {
                        string = params[0].split("=")[1];
                        if (params[1].startsWith("host")) {
                            host = params[1].split("=")[1];
                            port = params[2].split("=")[1];
                        } else {
                            host = params[2].split("=")[1];
                            port = params[1].split("=")[1];
                        }
                    }
                } catch (IndexOutOfBoundsException n) {
                    string = null;
                }
                String responseBody;
                String response;
                try {
                    responseBody = TCPClient.askServer(host, Integer.parseInt(port), string);
                    response = "HTTP/1.1 200 OK\r\n" +
                            "Server: TestServer\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "Connection: close\r\n" +
                            String.format("Content-Length: %d\r\n\r\n", responseBody.getBytes(StandardCharsets.UTF_8).length) +
                            responseBody;
                } catch (UnknownHostException h) {
                    response = "HTTP/1.1 404 Not Found\r\n" +
                            "\r\n" +
                            "Incorrect hostname!\r\n";
                } catch (ConnectException i) {
                    return;
                }


                send.write(response.getBytes(StandardCharsets.UTF_8));


            } else {
                String response = "HTTP/1.1 400 Bad Request\r\n" +
                        "\r\n" +
                        "Bad HTTP request!\r\n";
                send.write(response.getBytes(StandardCharsets.UTF_8));
            }

            receive.close();
            send.close();
            socket.close();
        }
        catch(IOException e){
            System.err.println(e);
        }

    };




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




