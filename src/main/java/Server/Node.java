package Server;

import java.util.concurrent.ExecutionException;

public class Node {

    ExecutorRunner exampleExecutorRunner;
    JobQueue queue;

    public Node() {
        exampleExecutorRunner = new ExecutorRunner();
        queue = new JobQueue();
    }

    public void pushJobInQueue(Job job) {
        queue.addNewJob(job);
    }

    public Job extractNextJob(){
        return queue.extractNextJob();
    }

    public void runThatExecutor(Job toExecute) {
        exampleExecutorRunner.executeJob(toExecute);
    }

    public int getExecutingJobId() throws ExecutionException, InterruptedException {
        Job jobCopy = (Job) exampleExecutorRunner.getFut().get();
        return jobCopy.getJobId();
    }

    public Boolean isJobDone() {
        return exampleExecutorRunner.getFut().isDone();
    }

    public int getQueueSize() {
        return queue.numJobs();
    }

    public Job removeJob() {
        return queue.removeJob();
    }


}
