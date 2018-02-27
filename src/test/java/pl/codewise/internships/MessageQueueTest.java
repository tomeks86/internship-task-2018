package pl.codewise.internships;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class MessageQueueTest {
    private InMemoryMessageQueue testedQueue;
    private static final int _5MINUTES = 1000 * 60 * 5;
    private String[] userAgents = {"Mozilla/5.0 (X11; U; Linux i686; pl-PL; rv:1.7.10) Gecko/20050717 Firefox/1.0.6", "Mozilla/4.0 (compatible; MSIE 6.0; X11; Linux i686; en) Opera 8.01", "Mozilla/5.0 (Macintosh; U; PPC; ja-JP; rv:1.0.1) Gecko/20020823 Netscape/7.0", "Mozilla/5.0 (compatible; Konqueror/3.3; Linux) (KHTML, like Gecko)", "Wget/1.9.1", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36 OPR/19.0.1326.56", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.12 (KHTML, like Gecko) Maxthon/3.0 Chrome/26.0.1410.43 Safari/535.12"};
    private int[] noErrorCodes = {100, 101, 102, 103, 200, 201, 202, 203, 204, 205, 206, 207, 208, 226, 300, 301, 302, 303, 304, 305, 306, 307, 308};
    private int[] errorCodes = {404, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 421, 422, 423, 424, 426, 428, 429, 431, 451, 500, 501, 502, 503, 504, 505, 506, 507, 508, 510, 511};
    private Random random = new Random();
    private DateTime startTime;
    private DateTime selectedEndTime;
    private DateTime selectedEndTime2;
    private Message selectedMessage;
    private Message selectedMessage2;
    private Snapshot snapshot;

    @Before
    public void setUp() {
        testedQueue = new InMemoryMessageQueue();
        snapshot = new Snapshot();
        startTime = DateTime.now().minusMillis(_5MINUTES);
        DateTime endTime;
        selectedMessage = new Message(userAgents[random.nextInt(userAgents.length)], errorCodes[random.nextInt(errorCodes.length)]);
        selectedEndTime = startTime.minusMillis(random.nextInt(_5MINUTES));
        snapshot.addToSnapshot(selectedEndTime, selectedMessage);
        testedQueue.addWithTime(selectedEndTime, selectedMessage);
        selectedMessage2 = new Message(userAgents[random.nextInt(userAgents.length)], errorCodes[random.nextInt(errorCodes.length)]);
        selectedEndTime2 = startTime.minusMillis(random.nextInt(_5MINUTES) + _5MINUTES + 5000);
        snapshot.addToSnapshot(selectedEndTime2, selectedMessage2);
        testedQueue.addWithTime(selectedEndTime2, selectedMessage2);

        for (int i = 0; i < 50; i++) {
            endTime = startTime.minusMillis(random.nextInt(_5MINUTES));
            testedQueue.addWithTime(endTime, new Message(userAgents[random.nextInt(userAgents.length)], noErrorCodes[random.nextInt(noErrorCodes.length)]));
            Message errorMessage = new Message(userAgents[random.nextInt(userAgents.length)], errorCodes[random.nextInt(errorCodes.length)]);
            testedQueue.addWithTime(endTime, errorMessage);
            snapshot.addToSnapshot(endTime, errorMessage);
        }

        for (int i = 0; i < 150; i++) {
            endTime = startTime.minusMillis(_5MINUTES + random.nextInt(_5MINUTES) + 15);
            testedQueue.addWithTime(endTime, new Message(userAgents[random.nextInt(userAgents.length)], noErrorCodes[random.nextInt(noErrorCodes.length)]));
            testedQueue.addWithTime(endTime, new Message(userAgents[random.nextInt(userAgents.length)], errorCodes[random.nextInt(errorCodes.length)]));
        }

    }

    @Test
    public void shouldFind50ErrorMessages() {
        assertEquals(51, testedQueue.numberOfErrorMessages(startTime));
    }

    @Test
    public void snapshotsShouldFindMessage() {
        assertTrue(testedQueue.snapshot(startTime).getMessages().containsKey(selectedEndTime));
        assertTrue(testedQueue.snapshot(startTime).getMessages().containsValue(selectedMessage));
    }

    @Test
    public void snapshotsShouldNotFindMessage() {
        assertFalse(testedQueue.snapshot(startTime).getMessages().containsKey(selectedEndTime2));
        assertFalse(testedQueue.snapshot(startTime).getMessages().containsKey(selectedEndTime2));
    }
}