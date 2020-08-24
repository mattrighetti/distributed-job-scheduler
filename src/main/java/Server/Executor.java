package Server;

import java.util.Deque;
import java.util.concurrent.*;

public class Executor implements Runnable {
    private final Deque<Job> jobDeque;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public Executor(Deque<Job> jobDeque) {
        this.jobDeque = jobDeque;
    }

    @Override
    public void run() {
        // TODO exec first job if deque is not empty, otherwise check if new jobs are added every second
    }
}
