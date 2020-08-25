package Server;

import java.util.concurrent.Callable;

public class Job implements Callable<String> {
    public final String jobId;
    public final int milliseconds;

    public Job (String jobId, int milliseconds) {
        this.jobId = jobId;
        this.milliseconds = milliseconds;
    }

    public String call() throws Exception {
        Thread.sleep(this.milliseconds);
        return "Executed Job[" + this.jobId + "] for " + this.milliseconds + " milliseconds";
    }
}
