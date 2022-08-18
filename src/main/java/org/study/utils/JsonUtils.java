package org.study.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {
    public static JsonElement getJsonElement(String content, String name) {
        JsonObject jsonObject = JsonParser.parseString(content).getAsJsonObject();
        return getJsonElement(jsonObject, name);
    }

    public static JsonElement getJsonElement(JsonObject jsonObject, String name) {
        if (jsonObject.has(name)) {
            JsonElement elem = jsonObject.get(name);
            if (elem.isJsonNull())
                elem = null;
            return elem;
        }
        return null;
    }

//    public static JsonElement getFirstJsonElement(JsonObject jsonObject) {
//        Iterator<Map.Entry<String, JsonElement>> it = jsonObject.entrySet().iterator();
//        if (it.hasNext()){
//
//        }
//        return null;
//    }
}
