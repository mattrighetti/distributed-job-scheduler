package Server.LoadBalancer;

import Server.Job;

import java.util.Deque;

public class JobDispatcher {
    private final Deque<Job> globalJobDeque;

    public JobDispatcher(final Deque<Job> globalJobDeque) {
        this.globalJobDeque = globalJobDeque;
    }
}
