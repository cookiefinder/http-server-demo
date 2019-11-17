package bio.server;

import bio.handler.BIOHttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOHttpServer {
    public static void main(String[] args) throws IOException {
        // 创建缓存线程池
        ExecutorService pool = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(9002)) {
            System.out.println("服务端启动，监听9002端口...");
            while (!Thread.interrupted()) {
                Socket socketClient = server.accept();
//                communicateWithClient(socketClient);
//                 通过线程池启动线程处理Http请求
                acceptHttpRequest(pool, socketClient);
            }
        } finally {
            pool.shutdown();
        }
    }

    private static void communicateWithClient(Socket socketClient) throws IOException {
        InputStream inputStream = socketClient.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String msg = reader.readLine();
        System.out.println(msg);
        // 向客户端发送信息
        OutputStream outputStream = socketClient.getOutputStream();
        outputStream.write("[Server]: hi".getBytes());
        socketClient.shutdownOutput();
    }

    private static void acceptHttpRequest(ExecutorService pool, Socket requestSocket) {
        pool.execute(new BIOHttpHandler(requestSocket));
    }
}
