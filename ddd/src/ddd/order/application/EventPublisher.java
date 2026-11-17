package ddd.order.application;

import java.util.ArrayList;
import java.util.List;

public class EventPublisher {

    private final List<EventHandler> handlers = new ArrayList<>();
    private final List<Object> waitingEvents = new ArrayList<>();

    public void subscribe(EventHandler handler) {
        handlers.add(handler);
    }

    public void publish(List<Object> events) {
        waitingEvents.addAll(events);
    }

    public void processEvents() {
        List<Object> events = new ArrayList<>(waitingEvents);
        waitingEvents.clear();
        for (Object event : events) {
            for (EventHandler handler : handlers) {
                handler.handle(event);
            }
        }
    }
}
