package org.neuralcoder.teleflux.core.api.util;

import java.time.Duration;

public interface Scheduler {

    interface ScheduledHandle {
        boolean cancel();
    }

    ScheduledHandle schedule(Runnable task, Duration delay);

    ScheduledHandle scheduleAtFixedRate(Runnable task, Duration initialDelay, Duration period);
}