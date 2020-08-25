package Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JobDao implements Dao<Job> {
    private List<Job> jobs = new ArrayList<>();

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
    public void update(Job job, String[] params) {
        // TODO update job (is it needed?)
    }

    @Override
    public void delete(Job job) {
        jobs.remove(job);
    }
}
