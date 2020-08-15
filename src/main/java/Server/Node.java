package Server;

import java.util.concurrent.ExecutionException;


public class Node {

    ExecutorRunner exampleExecutorRunner = new ExecutorRunner();

    public void runThatExecutor(Job jobToBeExecuted) throws ExecutionException, InterruptedException {
        exampleExecutorRunner.executeJob(jobToBeExecuted);
    }



}
