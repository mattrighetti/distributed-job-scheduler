package Server;

import java.util.concurrent.Callable;

public class Job extends Thread implements Callable {
    private int jobId;

    public Job () {
        jobId = (int)(Math.random()*10000);
    }

    public int getJobId() {
        return jobId;
    }

    public Object call() throws Exception {
        this.sleep(10000);
        return this;
    }
}
