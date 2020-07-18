package com.meaninglink;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

class SaveLoad {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson;

    SaveLoad(Context context) {
        sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    void save(ArrayList<Note> notes) {
        editor = sharedPreferences.edit();
        String strNotes = gson.toJson(notes);
        editor.putString("note", strNotes);
        editor.apply();
    }

    void save(HashMap<String,Word> dict) {
        editor = sharedPreferences.edit();
        String strDict = gson.toJson(dict);
        editor.putString("dict", strDict);
        editor.apply();
    }

    ArrayList<Note> loadNotes() {
        String strNotes = sharedPreferences.getString("note", "");
        if(!strNotes.equals("")) {
            Type type = new TypeToken<ArrayList<Note>>(){}.getType();
            return gson.fromJson(strNotes, type);
        }
        return new ArrayList<>();
    }

    HashMap<String,Word> loadDictionary() {
        String strDict = sharedPreferences.getString("dict", "");
        if(!strDict.equals("")) {
            Type type = new TypeToken<HashMap<String,Word>>(){}.getType();
            return gson.fromJson(strDict, type);
        }
        return new HashMap<>();
    }
}
