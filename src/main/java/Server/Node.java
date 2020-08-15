package Server;

public class Node {

    ExecutorRunner exampleExecutorRunner;
    Queue queue;

    public Node() {
        exampleExecutorRunner = new ExecutorRunner();
        queue = new Queue();
    }

    public void runThatExecutor(Job jobToBeExecuted) {
        exampleExecutorRunner.executeJob(jobToBeExecuted);
    }



}
