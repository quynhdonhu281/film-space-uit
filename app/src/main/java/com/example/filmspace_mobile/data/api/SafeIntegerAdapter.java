package com.example.filmspace_mobile.data.api;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class SafeIntegerAdapter extends TypeAdapter<Integer> {
    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
        if (value == null) {
            out.value(0);
        } else {
            out.value(value);
        }
    }

    @Override
    public Integer read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        
        switch (token) {
            case NUMBER:
                try {
                    return in.nextInt();
                } catch (NumberFormatException e) {
                    // If it's a double, try to convert it
                    try {
                        return (int) in.nextDouble();
                    } catch (Exception ex) {
                        return 0;
                    }
                }
            case STRING:
                String value = in.nextString();
                if (value == null || value.trim().isEmpty()) {
                    return 0;
                }
                try {
                    return Integer.parseInt(value.trim());
                } catch (NumberFormatException e) {
                    // Try parsing as double then converting to int
                    try {
                        return (int) Double.parseDouble(value.trim());
                    } catch (NumberFormatException ex) {
                        return 0;
                    }
                }
            case NULL:
                in.nextNull();
                return 0;
            default:
                in.skipValue();
                return 0;
        }
    }
}
