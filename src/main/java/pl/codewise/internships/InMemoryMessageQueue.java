package pl.codewise.internships;

import org.joda.time.DateTime;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryMessageQueue implements MessageQueue {
    private static final long _5MINUTES = 1000 * 60 * 5;
    private static ConcurrentHashMap<DateTime, Message> messages;

    InMemoryMessageQueue() {
        messages = new ConcurrentHashMap<DateTime, Message>();
    }

    @Override
    public void add(Message message) {
        messages.put(DateTime.now(), message);
    }

    public void addWithTime(DateTime dateTime, Message message) {
        messages.put(dateTime, message);
    }

    @Override
    public Snapshot snapshot() {
        DateTime now = DateTime.now();
        return new Snapshot(messages.entrySet().parallelStream()
                .filter(d -> (now.getMillis() - d.getKey().getMillis()) < _5MINUTES)
                .sorted(Map.Entry.comparingByKey(Collections.reverseOrder()))
                .limit(100)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (o, n) -> o, LinkedHashMap::new)));
    }

    public Snapshot snapshot(DateTime dateTime) {
        return new Snapshot(messages.entrySet().parallelStream()
                .filter(d -> (dateTime.getMillis() - d.getKey().getMillis()) < _5MINUTES)
                .sorted(Map.Entry.comparingByKey(Collections.reverseOrder()))
                .limit(100)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (o, n) -> o, LinkedHashMap::new)));
    }

    @Override
    public long numberOfErrorMessages() {
        DateTime now = DateTime.now();
        return messages.entrySet().stream()
                .filter(d -> (now.getMillis() - d.getKey().getMillis()) < _5MINUTES)
                .filter(d -> (d.getValue().getErrorCode() / 100) > 3)
                .count();
    }

    public long numberOfErrorMessages(DateTime dateTime) {
        return messages.entrySet().stream()
                .filter(d -> (dateTime.getMillis() - d.getKey().getMillis()) < _5MINUTES)
                .filter(d -> (d.getValue().getErrorCode() / 100) > 3)
                .count();
    }
}
