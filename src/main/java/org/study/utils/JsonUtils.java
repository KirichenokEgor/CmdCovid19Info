package org.study.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {

    /**
     * searches for element in String representation of JSON
     *
     * @param content String representation of JSON
     * @param name    Name of element to find
     * @return element or null if nothing was found or element is null
     */
    public static JsonElement getJsonElement(String content, String name) {
        JsonElement el = JsonParser.parseString(content);
        if (el == null || el.isJsonNull()) return null;
        JsonObject jsonObject = el.getAsJsonObject();
        return getJsonElement(jsonObject, name);
    }

    /**
     * searches for element in JsonObject
     *
     * @param jsonObject
     * @param name       Name of element to find
     * @return element or null if nothing was found or element is null
     */
    private static JsonElement getJsonElement(JsonObject jsonObject, String name) {
        if (jsonObject.has(name)) {
            JsonElement elem = jsonObject.get(name);
            if (elem.isJsonNull())
                elem = null;
            return elem;
        }
        return null;
    }
}
