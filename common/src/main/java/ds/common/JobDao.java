package ds.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

public class JobDao implements Storable<Deque<Job>> {
    private final Deque<Job> jobs;
    private final String filename;

    public static final Logger log = LogManager.getLogger(JobDao.class.getName());

    public JobDao(String filename) {
        this.filename = filename;
        this.jobs = readFromFile();
    }

    @Override
    public Deque<Job> readFromFile() {
        Optional<Deque<Job>> dequeOptional = Optional.ofNullable(FileStorage.readObjFromFile(filename));
        return dequeOptional.orElseGet(ConcurrentLinkedDeque::new);
    }

    @Override
    public void saveToFile() {
        FileStorage.writeObjToFile(this.jobs, this.filename);
    }

    public Deque<Job> get() {
        return readFromFile();
    }

    @StateChanging
    public void add(Job job) {
        this.jobs.add(job);
        saveToFile();
    }

    @StateChanging
    public Job removeFirst() {
        Job job = this.jobs.removeFirst();
        saveToFile();
        return job;
    }

    @StateChanging
    public void addLast(Job job) {
        this.jobs.addLast(job);
        saveToFile();
    }

    public int size() {
        return this.jobs.size();
    }

    public boolean isEmpty() {
        return this.jobs.isEmpty();
    }

    public Job getFirst() {
        return this.jobs.getFirst();
    }
}
