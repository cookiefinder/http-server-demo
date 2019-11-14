package nio.client;

import nio.helper.HandleSelectorEventHelper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Objects;

import static nio.Constant.IP;
import static nio.Constant.MESSAGE_ENCODING;
import static nio.Constant.PORT;

public class NIOHttpClient {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        SocketChannel clientSocketChannel = SocketChannel.open();
        clientSocketChannel.connect(new InetSocketAddress(IP, PORT));
        clientSocketChannel.configureBlocking(false);
        clientSocketChannel.register(selector, SelectionKey.OP_READ);
        newLoopListenChannelHandler(selector);
        clientSocketChannel.write(Charset.forName(MESSAGE_ENCODING).encode("hello"));
    }

    private static void newLoopListenChannelHandler(Selector selector) {
        new Thread(() -> {
            try {
                while(true) {
                    int preparedChannels = selector.select();
                    if (preparedChannels == 0) {
                        continue;
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        if (selectionKey.isReadable()) {
                            HandleSelectorEventHelper.handleReadableEvent(selector, selectionKey);
                        }
                    }
                }
            } catch (IOException e) {
                try {
                    Objects.requireNonNull(selector).close();
                } catch (IOException ioe) {
                    System.out.println("关闭selector异常: " + e.getMessage());
                }
                System.out.println("HTTP Client 异常退出... " + e.getMessage());
            }
        }).start();
    }
}
