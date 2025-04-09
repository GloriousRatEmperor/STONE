package components.gamestuff.Deserializer;

import components.SubComponents.SubComponent;
import com.google.gson.*;
import components.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ComponentDeserializer implements JsonSerializer<Component>,
        JsonDeserializer<Component> {

    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            Component comp=context.deserialize(element, Class.forName(type));
            JsonArray subComponents = jsonObject.getAsJsonArray("SUBS");
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
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
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
        result.add("SUBS", context.serialize(serialcomps));
        return result;
    }
}
