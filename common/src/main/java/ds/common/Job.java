package ds.common;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class Job implements Callable<String>, Serializable {
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

    @Override
    public String toString() {
        return "Job{" +
                "jobId='" + jobId + '\'' +
                ", milliseconds=" + milliseconds +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Job)) {
            return false;
        }
        return ((Job) obj).jobId.equals(this.jobId) && ((Job) obj).milliseconds == this.milliseconds;
    }

    public String getJobId() {
        return jobId;
    }
}
