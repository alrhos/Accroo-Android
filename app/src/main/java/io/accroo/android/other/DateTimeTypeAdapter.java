package io.accroo.android.other;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;

import java.lang.reflect.Type;

public class DateTimeTypeAdapter implements JsonSerializer<DateTime>,
        JsonDeserializer<DateTime> {
    @Override
    public DateTime deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
        //return DateTime.parse(json.getAsString());
        //return DateTime.parse(json.getAsLong());
        return new DateTime(json.getAsLong());
    }

    @Override
    public JsonElement serialize(DateTime src, Type typeOfSrc,
                                 JsonSerializationContext context) {
//        return new JsonPrimitive(ISODateTimeFormat
//                .dateTimeNoMillis()
//                .print(src));
        return new JsonPrimitive(src.getMillis());
    }

}