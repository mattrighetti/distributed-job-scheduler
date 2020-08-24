package Server;

import Utils.Tuple2;

import java.util.HashMap;

public interface JobDAO {
    public HashMap<String, Tuple2<Integer, String>> getAllJobsResults();

    public Tuple2<Integer, String> getJobData();

    public void deleteJob(String jobId);

    public void updateJob(String jobId);
}
