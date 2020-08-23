package test;
import Server.ExecutorRunner;
import Server.Job;
import Server.JobDeque;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class ExecutorTESTS {

    JobDeque jobDeque;
    ExecutorRunner executorRunner;
    Job jobOne;
    Job jobTwo;
    Job jobThree;
    Job jobFour;
    Job jobFive;

    @BeforeEach
    public void setUp() {
        jobDeque = new JobDeque();
        executorRunner = new ExecutorRunner();
        jobOne = new Job();
        jobTwo = new Job();
        jobThree = new Job();
        jobFour = new Job();
        jobFive = new Job();
        jobDeque.addNewJob(jobOne);
        jobDeque.addNewJob(jobTwo);
        jobDeque.addNewJob(jobThree);
        jobDeque.addNewJob(jobFour);
        jobDeque.addNewJob(jobFive);
    }

    @Test
    public void checkJobId() {
        System.out.println(jobOne.getJobId());
        System.out.println(jobTwo.getJobId());
        System.out.println(jobThree.getJobId());
        System.out.println(jobFour.getJobId());
        System.out.println(jobFive.getJobId());
    }


    @Test
    public void checkExecutor() throws ExecutionException, InterruptedException {

        executorRunner.executeJob(jobDeque.extractNextJob());
        Job test = (Job) executorRunner.getFut().get();
        System.out.println("Executing job: " + test.getJobId());
        System.out.println("Executing job: " + jobOne.getJobId());

        Assertions.assertEquals(true,jobOne.getJobId()==test.getJobId());

    }

}
