package ds.common;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class FileStorageTest {
    ArrayList<Job> jobQueue;

    @Test
    public void writeOneObject() {
        jobQueue = new ArrayList<>();
        Job test = new Job("jobID", 5000);
        Job readTest = null;

        try {
            FileStorage.writeObjToFile(test, "./file1", true);
            readTest = (Job) FileStorage.readObjFromFile("./file1", true).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(test, readTest);
    }

    @Test
    public void readOneObject() {
        jobQueue = new ArrayList<>();
        Job test1 = new Job("jobID", 5000);
        Job test2 = null;

        try {
            FileStorage.writeObjToFile(test1, "./file2", true);
            test2 = (Job) FileStorage.readObjFromFile("./file2", true).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(Objects.requireNonNull(test2).getJobId(), test1.getJobId());
        assertEquals(test2, test1);
    }

    @Test
    public void eReadTestFailure() {
        assertFalse(FileStorage.readObjFromFile("./this-file-does-not-exist", true).isPresent());
    }

    @Test
    public void stressTest() {
        jobQueue = new ArrayList<>();
        for (int i = 0; i < 700; i++) {
            Job test = new Job(Integer.toString(i), 5000);
            jobQueue.add(test);
        }
        for (int i = 0; i < 700; i++) {
            try {
                FileStorage.writeObjToFile(jobQueue.get(i), "./file3", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
