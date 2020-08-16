package Server;

import java.util.concurrent.ExecutionException;

public class Node {

    ExecutorRunner exampleExecutorRunner;
    JobQueue queue;

    public Node() {
        exampleExecutorRunner = new ExecutorRunner();
        queue = new JobQueue();
    }


    public void runThatExecutor() {
        exampleExecutorRunner.executeJob(queue.extractNewJob());
    }

    public int getExecutingJobId() throws ExecutionException, InterruptedException {
        Job jobCopy = (Job) exampleExecutorRunner.getFut().get();
        return jobCopy.getJobId();
    }



}
