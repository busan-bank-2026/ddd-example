package ddd.order.infrastructure;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import ddd.order.application.MessageSender;

public class SocketMessageSender implements MessageSender {

    private final String host;
    private final int port;

    public SocketMessageSender(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void send(String message) {
        try (Socket socket = new Socket(host, port);
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            writer.println(message);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
