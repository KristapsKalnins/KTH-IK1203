import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TCPClient {
    static int buffer = 64 * 1024;

    public static String askServer(String hostname, int port, String ToServer) throws IOException {
        if (ToServer == null) {
            return askServer(hostname, port);
        } else {
            ArrayList<Byte> inbuff = new ArrayList<>();
            inbuff.ensureCapacity(buffer);
            Socket socket = new Socket(hostname, port);
            socket.setSoTimeout(3 * 1000);
            OutputStream send = socket.getOutputStream();
            InputStream receive = socket.getInputStream();
            byte[] outputArray = (ToServer + '\n').getBytes(StandardCharsets.UTF_8);
            send.write(outputArray, 0, outputArray.length);


            int singleByte;
            String finalOut;


            try {
                while ((singleByte = receive.read()) != -1) {
                    inbuff.add((byte) singleByte);
                }
                inbuff.trimToSize();
                Byte[] wrapped = new Byte[inbuff.size()];
                inbuff.toArray(wrapped);

                byte[] finReadOut = new byte[wrapped.length];
                int i = 0;
                for (Byte b : wrapped) {
                    finReadOut[i++] = b;
                }
                finalOut = new String(finReadOut, StandardCharsets.UTF_8);


            } catch (SocketTimeoutException s) {
                inbuff.trimToSize();
                Byte[] wrapped = new Byte[inbuff.size()];
                inbuff.toArray(wrapped);

                byte[] finReadOut = new byte[wrapped.length];
                int i = 0;
                for (Byte b : wrapped) {
                    finReadOut[i++] = b;
                }
                finalOut = new String(finReadOut, StandardCharsets.UTF_8);

                send.close();
                receive.close();
                socket.close();
                return finalOut;
            }


            send.close();
            receive.close();
            socket.close();
            return finalOut;

        }
    }

    public static String askServer(String hostname, int port) throws IOException {
        ArrayList<Byte> inbuff = new ArrayList<>();
        inbuff.ensureCapacity(buffer);
        Socket socket = new Socket(hostname, port);
        socket.setSoTimeout(3 * 1000);
        InputStream receive = socket.getInputStream();


        int singleByte;
        String finalOut;


        try {
            while ((singleByte = receive.read()) != -1) {
                inbuff.add((byte) singleByte);
            }
            inbuff.trimToSize();
            Byte[] wrapped = new Byte[inbuff.size()];
            inbuff.toArray(wrapped);

            byte[] finReadOut = new byte[wrapped.length];
            int i = 0;
            for (Byte b : wrapped) {
                finReadOut[i++] = b;
            }
            finalOut = new String(finReadOut, StandardCharsets.UTF_8);



        } catch (SocketTimeoutException s) {
            inbuff.trimToSize();
            Byte[] wrapped = new Byte[inbuff.size()];
            inbuff.toArray(wrapped);

            byte[] finReadOut = new byte[wrapped.length];
            int i = 0;
            for (Byte b : wrapped) {
                finReadOut[i++] = b;
            }
            finalOut = new String(finReadOut, StandardCharsets.UTF_8);

            receive.close();
            socket.close();
            return finalOut;
        }


        receive.close();
        socket.close();
        return finalOut;

    }
}

