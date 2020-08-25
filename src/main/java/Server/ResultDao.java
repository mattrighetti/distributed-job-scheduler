package Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ResultDao implements Dao<Result> {
    private List<Result> results = new ArrayList<>();

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
    public void update(Result result, String[] params) {
        result.setJobId(Objects.requireNonNull(params[0], "Hash cannot be null"));
        result.setResultValue(Objects.requireNonNull(params[1], "Result cannot be null"));
        results.add(result);
    }

    @Override
    public void delete(Result result) {
        results.remove(result);
    }
}
