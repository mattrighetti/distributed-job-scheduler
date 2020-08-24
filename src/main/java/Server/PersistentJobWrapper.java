package Server;

import Utils.Tuple2;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PersistentJobWrapper implements JobDAO {
    private final Map<String, Tuple2<String, String>> jobPersistentStorage;

    public PersistentJobWrapper(Map<String, Tuple2<String, String>> jobPersistentStorage) {
        this.jobPersistentStorage = readFromFile();
    }

    public Map<String, Tuple2<String, String>> readFromFile() {
        File file = new File("serialized_hashmap");
        HashMap<String, Tuple2<String, String>> readValue = null;

        try (
                FileInputStream fis = new FileInputStream(file)
        ) {
            try (
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                readValue = (HashMap<String, Tuple2<String, String>>) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // TODO check if method works correctly
        return readValue;
    }

    public void saveToFile() {
        // TODO consider creating a new file if file is not found
        File file = new File("serialized_hashmap");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.jobPersistentStorage);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<String, Tuple2<Integer, String>> getAllJobsResults() {
        return null;
    }

    @Override
    public Tuple2<Integer, String> getJobData() {
        return null;
    }

    @Override
    public void deleteJob(String jobId) {
        this.jobPersistentStorage.remove(jobId);
        saveToFile();
    }

    @Override
    public void updateJob(String jobId) {
        // TODO change null to new jobValue
        this.jobPersistentStorage.put(jobId, null);
        saveToFile();
    }
}
