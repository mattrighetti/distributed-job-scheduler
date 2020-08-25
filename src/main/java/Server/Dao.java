package Server;

import Utils.Tuple2;

import java.util.HashMap;

public interface Dao<T> {
    HashMap<String, Tuple2<Integer, String>> getAllJobsResults();

    Tuple2<Integer, String> getJobData();

    void deleteJob(String jobId);

    void updateJob(String jobId);
}
