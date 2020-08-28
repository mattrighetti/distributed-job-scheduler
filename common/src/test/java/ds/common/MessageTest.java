package ds.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ds.common.Job;
import ds.common.Message;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;

public class MessageTest {

    @Test
    public void deserializeInteger() {
        Type integerTypeToken = new TypeToken<Message<Integer>>() { }.getType();
        String json = "{'status': 200, 'type': 'INFO', 'payload': -1}";

        Message<Integer> integerMessage = new Gson().fromJson(json, integerTypeToken);
        assertEquals(integerMessage.payload, -1);
    }

    @Test
    public void deserializeJob() {
        Type jobTypeToken = new TypeToken<Message<Job>>() { }.getType();
        String json = "{'status': 200, 'type': 'JOB', 'payload': {'jobId':'1234567', 'milliseconds':200}}";

        Message<Job> jobMessage = new Gson().fromJson(json, jobTypeToken);
        assertEquals(jobMessage.payload.jobId, "1234567");
        assertEquals(jobMessage.payload.milliseconds, 200);
    }

}
