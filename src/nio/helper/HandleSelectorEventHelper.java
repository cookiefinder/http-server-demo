package nio.helper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import static nio.Constant.MESSAGE_ENCODING;

public class HandleSelectorEventHelper {

    public static void handleReadableEvent(Selector selector, SelectionKey selectionKey) throws IOException {
        SocketChannel readableSocketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        StringBuilder requestMessage = new StringBuilder();
        while (readableSocketChannel.read(byteBuffer) > 0) {
            byteBuffer.flip();
            if (byteBuffer.hasRemaining()) {
                requestMessage.append(Charset.forName(MESSAGE_ENCODING).decode(byteBuffer));
            }
        }
        if (requestMessage.length() > 0) {
            System.out.println(requestMessage);
        }
        readableSocketChannel.register(selector, SelectionKey.OP_READ);
    }

    public static void handleAcceptedEvent(Selector selector,
                                           ServerSocketChannel serverSocketChannel,
                                           String needSentMsg) throws IOException {
        SocketChannel acceptedSocketChannel = serverSocketChannel.accept();
        acceptedSocketChannel.configureBlocking(false);
        acceptedSocketChannel.write(Charset.forName(MESSAGE_ENCODING).encode(needSentMsg));
        // 将此channel注册到selector上，并指定其为读事件
        acceptedSocketChannel.register(selector, SelectionKey.OP_READ);
    }
}
