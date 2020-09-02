package ds.cluster;

import ds.common.Job;
import ds.common.JobDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;

public class Executor implements Runnable {
    private final JobDao jobDeque;
    private final Map<String, String> resultsMap;
    private final Timer timer;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final Logger log = LogManager.getLogger(Executor.class.getName());

    public Executor(final JobDao jobDeque, final Map<String, String> resultsMap) {
        this.jobDeque = jobDeque;
        this.resultsMap = resultsMap;
        this.timer = new Timer();
    }

    /**
     * Triggers a TimerTask that checks every `period` if jobDeque has
     * available Jobs to consume.
     * @param period number of milliseconds to wait before the task is repeatedly being executed.
     */
    public void triggerTimerCheck(int period) {
        log.info("triggerTimerCheck");
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("Running executor timer");
                if (jobDeque.size() > 0) {
                    log.debug("Some jobs were found in the jobQue, starting consumer.");
                    consume();
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
                log.debug("Writing result to Map -> {}", result);
                resultsMap.put(job.jobId, result);
            } catch (InterruptedException | ExecutionException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        log.info("Queue is empty, running timer again");
    }

    @Override
    public void run() {
        consume();
    }
}
