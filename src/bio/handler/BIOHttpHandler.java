package bio.handler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BIOHttpHandler implements Runnable {
    private static Map<String, String> mediaTypeMap = new HashMap<>();
    private static final String WEB_ROOT = "/Users/zijie.jiang/twuc/webroot";
    static {
        mediaTypeMap.put("html", "text/html");
        mediaTypeMap.put("htm", "text/html");
        mediaTypeMap.put("jpg", "image/jpg");
        mediaTypeMap.put("jpeg", "image/jpeg");
        mediaTypeMap.put("gif", "image/gif");
        mediaTypeMap.put("js", "application/javascript");
        mediaTypeMap.put("css", "text/css");
        mediaTypeMap.put("json", "application/json");
        mediaTypeMap.put("mp3", "audio/mpeg");
        mediaTypeMap.put("mp4", "video/mp4");
    }
    private Socket client;
    private InputStream inputStream;
    private OutputStream outputStream;


    public BIOHttpHandler(Socket client) {
        this.client = client;
        try {
            inputStream = client.getInputStream();
            outputStream = client.getOutputStream();
        } catch (IOException e) {
            System.out.println("初始化字节流失败: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String uri = bufferedReader.readLine().split(" ")[1];//http://localhost:9002/
            if (uri.equals("/")) {
                uri += "index.html";
            }
            String contentType = uri.substring(uri.lastIndexOf('.') + 1);
            printWriter = new PrintWriter(outputStream);
            InputStream fileInputStream = new FileInputStream(WEB_ROOT + uri);
            flushResponse(printWriter, contentType, fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (printWriter != null) {
                    printWriter.close();
                }
                inputStream.close();
                outputStream.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void flushResponse(PrintWriter printWriter, String contentType, InputStream fileInputStream) throws IOException {
        printWriter.println("HTTP/1.1 200 ok");
        printWriter.println("Content-Type: " + mediaTypeMap.get(contentType) + ";charset=utf-8");
        printWriter.println("Content-Length: " + fileInputStream.available());
        printWriter.println("Server: hi, welcome!");
        printWriter.println("Date: " + new Date());
        printWriter.println();
        printWriter.flush();
        byte[] bytes = new byte[1024];// 1KB
        int len;
        while ((len = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, len);
        }
    }
}
