package ds.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JobDao implements Dao<Job> {
    private final List<Job> jobs;

    public JobDao(List<Job> jobs) {
        this.jobs = readFromFile("./jobs");
    }

    private ArrayList<Job> readFromFile(String filepath) {
        return null;
    }

    @Override
    public Optional<Job> get(long id) {
        return Optional.ofNullable(jobs.get((int) id));
    }

    @Override
    public List<Job> getAll() {
        return jobs;
    }

    @Override
    public void save(Job job) {
        jobs.add(job);
    }

    @Override
    public void delete(Job job) {
        jobs.remove(job);
    }
}
