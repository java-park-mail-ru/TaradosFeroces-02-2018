package application.mechanics.services;


import org.springframework.stereotype.Service;

@Service
public class MechanicsTimeService {
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
}
