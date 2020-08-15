package Server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorRunner {

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Boolean isBusy = false;

    public void executeJob(final Job job) {
        isBusy=true;
        executor.execute(new Runnable() {
            public void run() {
                try {
                    job.sleep(10000);
                    System.out.println("Job number " + job.getJobId() + " is done\n");
                    job.setIsDone();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public Boolean getBusy() {
        return isBusy;
    }
}
