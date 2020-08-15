package Server;

public class Node {

    ExecutorRunner exampleExecutorRunner = new ExecutorRunner();

    public void runThatExecutor(Job jobToBeExecuted) {
        exampleExecutorRunner.executeJob(jobToBeExecuted);
    }


}
