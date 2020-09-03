package ds.common.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ds.common.Message;

public class GsonUtils {
    public static final Gson shared = new GsonBuilder()
            .registerTypeAdapter(Message.class, new MessageDeserializer())
            .create();
}
