package ds.cluster;

import java.util.Optional;

public interface ClientSubmissionHandler {
    String handleJobSubmission(int milliseconds);

    Optional<String> handleResultRequest(String resultHash);

    String getAllStoredResults();
}
