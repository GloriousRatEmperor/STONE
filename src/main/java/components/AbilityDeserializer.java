package components;

import Abilitiess.Ability;
import com.google.gson.*;

import java.lang.reflect.Type;

public class AbilityDeserializer implements JsonSerializer<Ability>,
            JsonDeserializer<Ability> {

        @Override
        public Ability deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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
        public JsonElement serialize(Ability src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
            result.add("properties", context.serialize(src, src.getClass()));
            return result;
        }
    }