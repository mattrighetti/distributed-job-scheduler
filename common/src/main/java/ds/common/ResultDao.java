package ds.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResultDao implements Dao<Result> {
    private final List<Result> results;

    private final static Logger log = LogManager.getLogger(ResultDao.class.getName());

    public ResultDao(List<Result> results) {
        this.results = (List<Result>) FileStorage.readObjFromFile("./filePath", true).get();
    }

    private ArrayList<Result> readFromFile(String filePath) {
        return null;
    }

    @Override
    public Optional<Result> get(long id) {
        return Optional.ofNullable(results.get((int) id));
    }

    @Override
    public List<Result> getAll() {
        return results;
    }

    @Override
    public void save(Result result) {
        results.add(result);
    }

    @Override
    public void delete(Result result) {
        results.remove(result);
    }
}
