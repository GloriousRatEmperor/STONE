package components.gamestuff.Deserializer;

import com.google.gson.*;
import components.SubComponents.SubComponent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SubComponentDeserializer implements JsonSerializer<SubComponent>,
            JsonDeserializer<SubComponent> {

        @Override
        public SubComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            JsonElement element = jsonObject.get("properties");

            try {

                SubComponent comp=context.deserialize(element, Class.forName(type));
                JsonArray subComponents = jsonObject.getAsJsonArray("SUBSS");
                if(subComponents!=null) {
                    for (JsonElement e : subComponents) {
                        SubComponent c = context.deserialize(e, SubComponent.class);
                        comp.addSubComponent(c);
                    }
                }
                return comp;
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unknown element type: " + type, e);
            }
        }

        @Override
        public JsonElement serialize(SubComponent src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
            result.add("properties", context.serialize(src, src.getClass()));

            List<? extends SubComponent> subComps;
            subComps=src.getSubComponents();

            List<JsonElement> serialcomps=null;

            if(subComps!=null) {
                serialcomps = new ArrayList();
                for (SubComponent cmp : subComps) {
                    serialcomps.add(context.serialize(cmp, SubComponent.class));
                }

            }
            result.add("SUBSS", context.serialize(serialcomps));

            return result;
        }
    }