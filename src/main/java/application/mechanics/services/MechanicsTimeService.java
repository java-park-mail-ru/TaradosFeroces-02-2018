package application.mechanics.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class MechanicsTimeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MechanicsTimeService.class);

    private long millis = 0;

    public long reset() {
        final long prev = millis;
        millis = 0;
        return prev;
    }

    public long tick(long delta) {
        millis += delta;
        return millis;
    }

    public long time() {
        return millis;
    }

    public static void sleep(long millis) {
        if (millis <= 0) {
            return;
        }

        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
            LOGGER.info("sleep: millis=" + millis);
        }
    }
}
