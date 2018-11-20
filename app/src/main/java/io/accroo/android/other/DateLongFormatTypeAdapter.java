package io.accroo.android.other;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Date;

public class DateLongFormatTypeAdapter extends TypeAdapter<DateTime> {

    @Override
    public void write(JsonWriter out, DateTime value) throws IOException {
        out.value(value.toDateTime().toString());
    }

    @Override
    public DateTime read(JsonReader in) throws IOException {
        return new DateTime(in.nextLong());
    }

}