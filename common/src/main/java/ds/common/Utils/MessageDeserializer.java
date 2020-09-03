package ds.common.Utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import ds.common.Job;
import ds.common.Message;

import java.lang.reflect.Type;
import java.util.List;

public class MessageDeserializer implements JsonDeserializer<Message<?>> {
    @Override
    public Message<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement messageType = jsonObject.get("messageType");

        Message.MessageType type = GsonUtils.shared.fromJson(messageType.getAsString(), Message.MessageType.class);
        Type typeToken = null;

        switch (type) {
            case JOB:
                typeToken = new TypeToken<Message<Job>>() {}.getType();
                break;
            case INFO:
                typeToken = new TypeToken<Message<Integer>>() {}.getType();
                break;
            case RESULT:
                typeToken = new TypeToken<Message<List<Tuple2<String, String>>>>() {}.getType();
                break;
            case RES_REQ:
                typeToken = new TypeToken<Message<Tuple2<List<Tuple2<String, String>>, List<String>>>>() {}.getType();
                break;
            case REQUEST_OF_RES:
                typeToken = new TypeToken<Message<List<String>>>() {}.getType();
                break;
        }

        return new Gson().fromJson(jsonObject, typeToken);
    }

}
