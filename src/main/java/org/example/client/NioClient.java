package org.example.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class NioClient {
    public static void main(String[] args) throws IOException, InterruptedException {

        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 1111);
        try (SocketChannel socketChannel = SocketChannel.open(serverAddress)) {

            log("Connecting to Server on port 1111...");

            ArrayList<String> companyDetails = new ArrayList<String>();

            // create a ArrayList with companyName list
            companyDetails.add("Facebook");
            companyDetails.add("Twitter");
            companyDetails.add("IBM");
            companyDetails.add("Google");
            companyDetails.add("EXIT");

            for (String companyName : companyDetails) {

                byte[] message = new String(companyName).getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(message);
                socketChannel.write(buffer);

                log("sending: " + companyName);
                buffer.clear();

                // wait for 2 seconds before sending next message
                Thread.sleep(2000);
            }
        }
    }

    private static void log(String str) {
        System.out.println(str);
    }
}
