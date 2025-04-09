package components.gamestuff.Deserializer;

import Multiplayer.DataPacket.ClientData;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ClientDataSerializer implements JsonSerializer<ClientData>,
        JsonDeserializer<ClientData> {
    @Override
    public ClientData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");
        try {
            ClientData comp=context.deserialize(element, Class.forName(type));
            return comp;
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + type, e);
        }
    }

    @Override
    public JsonElement serialize(ClientData src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        result.add("properties", context.serialize(src, src.getClass()));
        return result;
    }
}
