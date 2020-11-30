package org.example.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) throws IOException {

        // Selector: multiplexor of SelectableChannel objects
        Selector selector = Selector.open(); // selector is open here

        // ServerSocketChannel: selectable channel for stream-oriented listening sockets
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 1111);

        // Binds the channel's socket to a local address and configures the socket to listen for connections
        serverSocketChannel.bind(serverAddress);

        // Adjusts this channel's blocking mode.
        serverSocketChannel.configureBlocking(false);

        int ops = serverSocketChannel.validOps();
        SelectionKey selectKey = serverSocketChannel.register(selector, ops, null);

        // Infinite loop..
        // Keep server running
        while (true) {

            log("i'm a server and i'm waiting for new connection and buffer select...");
            // Selects a set of keys whose corresponding channels are ready for I/O operations
            selector.select();

            // token representing the registration of a SelectableChannel with a Selector
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> selectedKeysIterator = selectedKeys.iterator();

            while (selectedKeysIterator.hasNext()) {
                SelectionKey myKey = selectedKeysIterator.next();

                // Tests whether this key's channel is ready to accept a new socket connection
                if (myKey.isAcceptable()) {
                    SocketChannel clientChannel = serverSocketChannel.accept();

                    // Adjusts this channel's blocking mode to false
                    clientChannel.configureBlocking(false);

                    // Operation-set bit for read operations
                    clientChannel.register(selector, SelectionKey.OP_READ);
                    log("Connection Accepted: " + clientChannel.getLocalAddress() + "\n");

                    // Tests whether this key's channel is ready for reading
                } else if (myKey.isReadable()) {

                    SocketChannel clientChannel = (SocketChannel) myKey.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    clientChannel.read(buffer);
                    String result = new String(buffer.array()).trim();

                    log("Message received: " + result);

                    if (result.equals("EXIT")) {
                        clientChannel.close();
                        log("\nIt's time to close connection as we got last message 'EXIT'");
                        log("\nServer will keep running. Try running client again to establish new connection");
                    }
                }
                selectedKeysIterator.remove();
            }
        }
    }

    private static void log(String str) {
        System.out.println(str);
    }
}
