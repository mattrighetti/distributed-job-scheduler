package ds.common;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;

public class FileStorageTest {
    FileStorage fileStorage;
    ArrayList<Job> jobQueue;

    @Test
    public void writeOneObject() {
        fileStorage = new FileStorage();
        jobQueue = new ArrayList<>();

        Job test = new Job("jobID", 5000);


        fileStorage.writeObjToFile(test);
    }

    @Test
    public void readOneObject() {
        fileStorage = new FileStorage();
        jobQueue = new ArrayList<>();

        Job test1 = new Job("jobID", 5000);
        fileStorage.writeObjToFile(test1);
        Job test2 = fileStorage.readObjFromFile();

        Assertions.assertEquals(test2.getJobId(), test1.getJobId());
        Assertions.assertEquals(test2, test1);
    }

    @Test
    public void stressTest() {
        fileStorage = new FileStorage();
        jobQueue = new ArrayList<>();
        for (int i = 0; i < 700; i++) {
            Job test = new Job(Integer.toString(i), 5000);
            jobQueue.add(test);
        }
        for (int i = 0; i < 700; i++) {
            fileStorage.writeObjToFile(jobQueue.get(i));
        }
    }

}
