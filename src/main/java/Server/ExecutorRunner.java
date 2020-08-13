package Server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorRunner {

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Thread jobThread = new Thread();
    Boolean isExecuted = false;

    public void executeJob(int jobId) {

        executor.execute(new Runnable() {
            public void run() {
                try {
                    jobThread.sleep(30000);
                    isExecuted = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public Boolean getExecuted() {
        return isExecuted;
    }
}
