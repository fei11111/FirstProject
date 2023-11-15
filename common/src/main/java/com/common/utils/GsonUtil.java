package com.vanrui.mqttlib.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author v-lihc01
 * @date 2019/4/23
 */
public class GsonUtil {

    private static Gson gson;

    public GsonUtil() {
    }

    public static Gson gson() {
        if (gson == null) {
            synchronized (Gson.class) {
                if (gson == null) {
                    gson = new GsonBuilder().disableHtmlEscaping().create();
                }
            }
        }
        return gson;
    }


    public static <T> List<T> parseList(String string, Class<T[]> clazz) {
        List<T> list = new ArrayList<>();
        try {
            T[] arr = gson().fromJson(string, clazz);
            if (arr != null) {
                list = Arrays.asList(arr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String toJson(Object objects) {
        String jsonStr = "";
        if (objects != null) {
            try {
                jsonStr = gson().toJson(objects);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonStr;
    }


    public static <T> T fromJson(String string, Class<T> clazz) {
        try {
            return gson().fromJson(string, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(String string, Type type) {
        try {
            return gson().fromJson(string, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
