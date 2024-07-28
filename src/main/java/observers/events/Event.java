package observers.events;

public class Event {
    public EventType type;
    public String strargs;
    public Event(EventType type) {
        this.type = type;
    }

    public Event() {
        this.type = EventType.UserEvent;
    }
}
