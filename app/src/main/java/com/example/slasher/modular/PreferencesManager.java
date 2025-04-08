package com.example.slasher.modular;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferencesManager {

    private static final String PREF_NAME = "app_settings";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // String érték mentése
    public void saveString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    // String érték lekérése
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    // Boolean érték mentése
    public void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Boolean érték lekérése
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    // Integer érték mentése
    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    // Integer érték lekérése
    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void saveBitmap(String key, Bitmap bitmap) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] byteArray = bos.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        editor.putString(key, encoded);
        editor.apply();
    }

    public Bitmap getBitmap(String key, Bitmap defaultValue) {
        String encoded = sharedPreferences.getString(key, null);
        if (encoded == null) {
            return defaultValue;
        }
        byte[] byteArray = Base64.decode(encoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    // JSON objektum mentése
    public void saveInventory(String key, Map<String, Integer> inventory) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        editor.putString(key, jsonObject.toString());
        editor.apply();
    }

    // JSON objektum lekérése
    public Map<String, Integer> getInventory(String key) {
        Map<String, Integer> inventory = new HashMap<>();
        String jsonString = sharedPreferences.getString(key, "{}");
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray keys = jsonObject.names();
            if (keys != null) {
                for (int i = 0; i < keys.length(); ++i) {
                    String itemKey = keys.getString(i);
                    int quantity = jsonObject.getInt(itemKey);
                    inventory.put(itemKey, quantity);
                }
            }
        } catch (JSONException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return inventory;
    }

    // Térkép mentése JSON-ként
    public void saveMap(String key, Map<Integer, List<Integer>> mapData) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<Integer, List<Integer>> entry : mapData.entrySet()) {
                JSONArray jsonArray = new JSONArray(entry.getValue());
                jsonObject.put(String.valueOf(entry.getKey()), jsonArray);
            }
        } catch (JSONException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        editor.putString(key, jsonObject.toString());
        editor.apply();
    }

    // Térkép visszaállítása JSON-ból
    public Map<Integer, List<Integer>> getMap(String key) {
        Map<Integer, List<Integer>> mapData = new HashMap<>();
        String jsonString = sharedPreferences.getString(key, "{}");
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray keys = jsonObject.names();
            if (keys != null) {
                for (int i = 0; i < keys.length(); ++i) {
                    int stepKey = Integer.parseInt(keys.getString(i));
                    JSONArray jsonArray = jsonObject.getJSONArray(keys.getString(i));
                    List<Integer> steps = new ArrayList<>();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        steps.add(jsonArray.getInt(j));
                    }
                    mapData.put(stepKey, steps);
                }
            }
        } catch (JSONException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return mapData;
    }


    // Törlés egy adott kulcs alapján
    public void removeKey(String key) {
        editor.remove(key);
        editor.apply();
    }

    // Minden beállítás törlése
    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}
