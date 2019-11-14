package bio.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class BIOHttpClient {
    public static void main(String[] args) throws IOException {
        Socket socketServer = new Socket("127.0.0.1", 9002);
        BufferedReader reader = null;
        OutputStream outputStream = null;
        try {
            outputStream = socketServer.getOutputStream();
            outputStream.write("[Client]: hello".getBytes());
            socketServer.shutdownOutput();// 通知服务端写出完毕
            InputStream inputStream = socketServer.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String msg = reader.readLine();
            System.out.println(msg);
            reader.close();
        } finally {
            socketServer.close();
            if (reader != null) {
                reader.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
