package ddd.order.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventPublisher {

    private final Map<Class<?>, List<Consumer<Object>>> subscribers = new HashMap<>();
    private final List<Object> waitingEvents = new ArrayList<>();

    public <T> void subscribe(Class<T> eventType, Consumer<T> subscriber) {
        subscribers.computeIfAbsent(eventType, type -> new ArrayList<>())
                .add(event -> subscriber.accept(eventType.cast(event)));
    }

    public void publish(List<?> events) {
        waitingEvents.addAll(events);
    }

    public void processEvents() {
        List<Object> events = new ArrayList<>(waitingEvents);
        waitingEvents.clear();
        for (Object event : events) {
            for (Consumer<Object> subscriber : subscribers.getOrDefault(event.getClass(), List.of())) {
                subscriber.accept(event);
            }
        }
    }
}
