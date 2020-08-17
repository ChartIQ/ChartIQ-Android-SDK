package com.chartiq.sdk;

import com.google.gson.Gson;

public class Util {
    public static String buildArgumentStringFromArgs(Object... args) {
        String s = new Gson().toJson(args);
        return s.substring(1, s.length() - 1);
    }
}
