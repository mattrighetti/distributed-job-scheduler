package Server;

import java.util.ArrayDeque;

public class JobDeque {

    private ArrayDeque<Job> queue;

    public JobDeque() {
        queue = new ArrayDeque<Job>();
    }

    public ArrayDeque<Job> getQueue() {
        return queue;
    }

    public Job extractNextJob() {
        return queue.removeFirst();
    }

    public void addNewJob(Job jobToAdd) {
        queue.addLast(jobToAdd);
    }

    public Job removeJob() {
        return queue.removeLast();
    }

}
