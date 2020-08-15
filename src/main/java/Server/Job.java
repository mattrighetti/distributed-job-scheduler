package Server;

import java.util.concurrent.Callable;

public class Job extends Thread implements Callable {
    private Boolean isDone;
    private int jobId;

    public Job () {
        jobId = (int)(Math.random()*10000);
        isDone = false;
    }

    public void setIsDone() {
        isDone=true;
    }

    public Boolean getDone() {
        return isDone;
    }

    public int getJobId() {
        return jobId;
    }

    public Object call() throws Exception {
        this.sleep(10000);
        return this;
    }
}
