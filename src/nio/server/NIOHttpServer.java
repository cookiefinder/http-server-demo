package nio.server;

import nio.helper.HandleSelectorEventHelper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

import static nio.Constant.PORT;

public class NIOHttpServer {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(PORT));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("HTTP SERVER 成功启动...");
        while(true) {
            try {
                int preparedChannels = selector.select();
                // 防止selector空轮询
                if (preparedChannels == 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    // 清除selector上所有注册的channel
                    iterator.remove();
                    if (selectionKey.isAcceptable()) {
                        HandleSelectorEventHelper.handleAcceptedEvent(selector, serverSocketChannel, "连接成功!");
                    }
                    if (selectionKey.isReadable()) {
                        HandleSelectorEventHelper.handleReadableEvent(selector, selectionKey);
                    }
                }
            } catch (Exception e) {
                System.out.println("HTTP SERVER 异常退出... " + e.getMessage());
                break;
            }
        }
        selector.close();
        serverSocketChannel.close();
    }
}
