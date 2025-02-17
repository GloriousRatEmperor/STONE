package components.gamestuff;

import components.SubComponents.SubComponent;
import com.google.gson.*;

import java.lang.reflect.Type;

public class SubComponentDeserializer implements JsonSerializer<SubComponent>,
            JsonDeserializer<SubComponent> {

        @Override
        public SubComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            JsonElement element = jsonObject.get("properties");

            try {
                return context.deserialize(element, Class.forName(type));
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unknown element type: " + type, e);
            }
        }

        @Override
        public JsonElement serialize(SubComponent src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
            result.add("properties", context.serialize(src, src.getClass()));
            return result;
        }
    }