package net.aung.sunshine.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by aung on 12/12/15.
 */
public class CommonInstances {

    private static Gson gson = new GsonBuilder()
            .create();

    public static Gson getGsonInstance() {
        return gson;
    }
}
