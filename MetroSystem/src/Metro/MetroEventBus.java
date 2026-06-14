package Metro;

import java.util.*;
import java.util.function.Consumer;

public class MetroEventBus {

    public enum Event {
        LINE_ADDED, LINE_UPDATED, LINE_REMOVED,
        STATION_ADDED, STATION_UPDATED, STATION_REMOVED,
        FARE_UPDATED, DISCOUNT_UPDATED,
        GATE_FAULT, GATE_ENABLED,
        TICKET_ISSUED, TICKET_REFUNDED
    }

    private static MetroEventBus instance;
    private final Map<Event, List<Consumer<Object>>> listeners = new EnumMap<>(Event.class);

    private MetroEventBus() {}

    public static MetroEventBus getInstance() {
        if (instance == null) instance = new MetroEventBus();
        return instance;
    }

    public void subscribe(Event event, Consumer<Object> listener) {
        listeners.computeIfAbsent(event, k -> new ArrayList<>()).add(listener);
    }

    /** Publish with a payload (e.g. the changed MetroLine). */
    public void publish(Event event, Object payload) {
        List<Consumer<Object>> subs = listeners.getOrDefault(event, Collections.emptyList());
        for (Consumer<Object> sub : new ArrayList<>(subs)) sub.accept(payload);
    }

    /** Publish with no payload. */
    public void publish(Event event) { publish(event, null); }

    public void unsubscribeAll(Event event) {
        listeners.remove(event);
    }
}