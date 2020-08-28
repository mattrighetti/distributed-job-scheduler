package ds.common;

import ds.common.FaultTolerance.FileStorage;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;

public class FaultToleranceTest {
    FileStorage fileStorage;
    ArrayList<Job> jobQueue;

    @Test
    public void writeOneObject() {
        fileStorage = new FileStorage();
        jobQueue = new ArrayList();


        Job test = new Job("jobID",5000);
        fileStorage.writeObjToFile(test);
    }

    @Test
    public void readOneObject() {
        fileStorage = new FileStorage();
        jobQueue = new ArrayList();

        Job test1 = new Job("jobID",5000);
        fileStorage.writeObjToFile(test1);
        Job test2 = fileStorage.readObjFromFile();

        Assertions.assertEquals(true,test1.getJobId().equals(test2.getJobId()));
        Assertions.assertEquals(true,test1.equals(test2));

    }

    @Test
    public void stressTest() {
        fileStorage = new FileStorage();
        jobQueue = new ArrayList();
        for(int i=0; i<700;i++) {
            Job test = new Job(Integer.toString(i),5000);
            jobQueue.add(test);
        }
        for(int i=0; i<700;i++) {
            fileStorage.writeObjToFile(jobQueue.get(i));
        }
    }

}
