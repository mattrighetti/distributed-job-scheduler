package Server;

public class Job extends Thread {
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
}
