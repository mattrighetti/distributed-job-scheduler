package Server;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorRunner {

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future fut;

    public void executeJob(Job jobToBeExecuted) {
        fut = executor.submit((Callable) jobToBeExecuted);
    }

    public Future getFut() {
        return fut;
    }
}
