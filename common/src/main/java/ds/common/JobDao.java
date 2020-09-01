package ds.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

public class JobDao implements Dao<Job> {
    private final Deque<Job> jobs;
    private final String filename;

    public static final Logger log = LogManager.getLogger(JobDao.class.getName());

    public JobDao(String filename) {
        this.filename = filename;
        this.jobs = readFromFile(filename);
    }

    public Deque<Job> readFromFile(String filename) {
        Optional<Deque<Job>> dequeOptional = FileStorage.readObjFromFile(filename, true);
        return dequeOptional.orElseGet(ConcurrentLinkedDeque::new);
    }

    public void saveToFile() {
        FileStorage.writeObjToFile(this.jobs, this.filename, true);
    }

    @Override
    public Deque<Job> get() {
        return readFromFile(this.filename);
    }

    @Override
    public void add(Job job) {
        this.jobs.add(job);
        saveToFile();
    }

    @Override
    public Job removeFirst() {
        Job job = this.jobs.removeFirst();
        saveToFile();
        return job;
    }

    @Override
    public void addLast(Job job) {
        this.jobs.addLast(job);
    }

    @Override
    public int size() {
        return this.jobs.size();
    }

    @Override
    public boolean isEmpty() {
        return this.jobs.isEmpty();
    }
}
