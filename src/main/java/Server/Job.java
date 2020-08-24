package Server;

import java.util.concurrent.Callable;

public class Job implements Callable<String> {
    private final String jobId;
    private final int milliseconds;

    public Job (String jobId, int milliseconds) {
        this.jobId = jobId;
        // TODO init milliseconds in constructor
        this.milliseconds = milliseconds;
    }

    public String getJobId() {
        return jobId;
    }

    public String call() throws Exception {
        Thread.sleep(this.milliseconds);
        return "Executed Job[" + this.jobId + "] for " + this.milliseconds + " milliseconds";
    }
}
