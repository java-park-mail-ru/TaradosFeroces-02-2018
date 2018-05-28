package application.tasks;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class WaveGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaveGenerator.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        LOGGER.info("The time is now {}", DATE_FORMAT.format(new Date()));
    }
}
