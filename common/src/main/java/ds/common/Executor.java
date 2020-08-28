package ds.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.Deque;
import java.util.TimerTask;
import java.util.concurrent.*;

public class Executor implements Runnable {
    private final Deque<Job> jobDeque;
    private final Timer timer;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final Logger log = LogManager.getLogger(Executor.class.getName());

    public Executor(final Deque<Job> jobDeque) {
        this.jobDeque = jobDeque;
        this.timer = new Timer();
    }

    /**
     * Triggers a TimerTask that checks every `period` if jobDeque has
     * available Jobs to consume.
     * @param period number of milliseconds to wait before the task is repeatedly being executed.
     */
    public void triggerTimerCheck(int period) {
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (jobDeque.size() > 0) {
                    log.debug("Some jobs were found in the jobQue, starting consumer.");
                    consume();
                    timer.cancel();
                } else {
                    log.debug("jobDeque is empty.");
                }
            }
        }, 0, period);
    }

    /**
     * Consumes every Job there is in the jobDeque.
     * If at a certain point jobDeque is out of Jobs, a TimerTask is started every second
     * to check if new jobs are available in jobDeque in order to consume less resources.
     */
    private void consume() {
        Future<String> future;
        Job job;
        String result;
        while (!jobDeque.isEmpty()) {
            try {
                job = this.jobDeque.removeFirst();
                log.debug("Running {}", job);
                future = executor.submit(job);
                result = future.get();
                log.debug(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        log.info("Queue is empty, starting timer.");
        triggerTimerCheck(1000);
    }

    @Override
    public void run() {
        consume();
    }
}
