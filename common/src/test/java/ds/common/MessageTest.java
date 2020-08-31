package ds.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ds.common.Utils.GsonUtils;
import ds.common.Utils.Tuple2;
import org.junit.jupiter.api.Test;

import static ds.common.Message.MessageType.JOB;
import static ds.common.Message.MessageType.RESULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MessageTest {

    @Test
    public void deserializeInteger() {
        Type integerTypeToken = new TypeToken<Message<Integer>>() { }.getType();
        String json = "{'status': 200, 'messageType': 'INFO', 'payload': -1}";

        Message<Integer> integerMessage = new Gson().fromJson(json, integerTypeToken);
        assertEquals(integerMessage.payload, -1);
    }

    @Test
    public void deserializeJob() {
        Type jobTypeToken = new TypeToken<Message<Job>>() { }.getType();
        String json = "{'status': 200, 'messageType': 'JOB', 'payload': {'jobId':'1234567', 'milliseconds':200}}";

        Message<Job> jobMessage = new Gson().fromJson(json, jobTypeToken);
        assertEquals(jobMessage.payload.jobId, "1234567");
        assertEquals(jobMessage.messageType, JOB);
        assertEquals(jobMessage.payload.milliseconds, 200);
    }

    @Test
    public void autoJobMessageDeserialization() {
        String jsonMessage = "{'status': 200, 'messageType': 'JOB', 'payload': {'jobId':'1234567', 'milliseconds':200}}";
        Message<?> message = GsonUtils.shared.fromJson(jsonMessage, Message.class);
        assertEquals(message.status, 200);
        assertEquals(message.messageType, JOB);
        assertEquals(message.payload, new Job("1234567", 200));
    }

    @Test
    public void autoResultMessageDeserialization() {
        List<String> requests = new ArrayList<>();
        requests.add("test1");
        requests.add("test2");

        List<Tuple2<String, String>> results = new ArrayList<>();
        results.add(new Tuple2<>("id1", "res1"));
        results.add(new Tuple2<>("id2", "res2"));
        results.add(new Tuple2<>("id3", "res3"));
        results.add(new Tuple2<>("id4", "res4"));

        Tuple2<List<Tuple2<String, String>>, List<String>> complexTuple = new Tuple2<>(results, requests);

        Message<Tuple2<List<Tuple2<String, String>>, List<String>>> message =
                new Message<>(200, RESULT, complexTuple);

        Type type = new TypeToken<Message<Tuple2<List<Tuple2<String, String>>, List<String>>>>() {}.getType();
        String json = GsonUtils.shared.toJson(message, type);

        Message<?> deserializedMessage = GsonUtils.shared.fromJson(json, Message.class);
        assertEquals(deserializedMessage.status, 200);
        assertEquals(deserializedMessage.messageType, RESULT);
        assertTrue(deserializedMessage.payload instanceof Tuple2);

        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) deserializedMessage.payload;

        assertTrue(tuple2.item1 instanceof List);
        assertTrue(tuple2.item2 instanceof List);

        List<?> resultList = (List<?>) tuple2.item1;
        List<?> requestList = (List<?>) tuple2.item2;

        assertTrue(resultList.get(0) instanceof Tuple2);
        assertEquals(requestList, requests);
        assertEquals(resultList, results);
    }

}
