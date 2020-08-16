package Server;

import java.util.ArrayDeque;

public class JobQueue {

    ArrayDeque<Job> queue = new ArrayDeque<Job>();

    public ArrayDeque<Job> getQueue() {
        return queue;
    }

    public Job extractNewJob() {
        Job toReturn = queue.removeFirst();
        return toReturn;
    }

    public void addNewJob(Job jobToAdd) {
        queue.addLast(jobToAdd);
    }

    public Job removeJob() {
        Job toReturn = queue.removeLast();
        return toReturn;
    }

    public int numJobs() {
        return queue.size();
    }


}
