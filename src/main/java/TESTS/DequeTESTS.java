package TESTS;
import Server.Job;
import Server.JobDeque;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DequeTESTS {


    JobDeque jobDeque;
    Job jobOne;
    Job jobTwo;
    Job jobThree;
    Job jobFour;
    Job jobFive;

    @BeforeEach
    public void setUp() {
        jobDeque = new JobDeque();
        jobOne = new Job();
        jobTwo = new Job();
        jobThree = new Job();
        jobFour = new Job();
        jobFive = new Job();
    }


    @Test
    public void checkDequeEmpty() {
        Boolean check = jobDeque.getQueue().isEmpty();
        Assertions.assertEquals(true, check);
    }

    @Test
    public void checkDequeNotEmpty() {
        jobDeque.addNewJob(jobOne);

        Boolean check = jobDeque.getQueue().isEmpty();
        Assertions.assertEquals(false, check);
    }

    @Test
    public void checkDequeSize() {
        jobDeque.addNewJob(jobOne);
        jobDeque.addNewJob(jobTwo);

        int size = jobDeque.getQueue().size();

        Assertions.assertEquals(2, size);
    }

    @Test
    public void checkJobExtraction() {
        jobDeque.addNewJob(jobFive);

        Job test = jobDeque.extractNextJob();

        Assertions.assertEquals(true, test.getJobId()==jobFive.getJobId());

    }

    @Test
    public void checkJobRemoval() {
        jobDeque.addNewJob(jobFive);
        jobDeque.addNewJob(jobTwo);

        Job test= jobDeque.removeJob();

        Assertions.assertEquals(true, test.getJobId()==jobTwo.getJobId());

    }

}
