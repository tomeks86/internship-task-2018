package pl.codewise.internships;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

public class Snapshot {
    private Map<DateTime, Message> messages;

    public Snapshot() {
        messages = new HashMap<>();
    }

    public Snapshot(Map<DateTime, Message> messages) {
        this.messages = messages;
    }

    public void addToSnapshot(DateTime dateTime, Message message) {
        messages.put(dateTime, message);
    }

    public Map<DateTime, Message> getMessages() {
        return messages;
    }
}
