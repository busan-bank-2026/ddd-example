package ddd.order.application;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SocketEventPublisher extends EventPublisher {

    private final String host;
    private final int port;

    public SocketEventPublisher(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void publish(List<?> events) {
        for (Object event : events) {
            if (event instanceof String message) {
                send(message);
            } else {
                super.publish(List.of(event));
            }
        }
    }

    private void send(String message) {
        try (Socket socket = new Socket(host, port);
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            writer.println(message);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
