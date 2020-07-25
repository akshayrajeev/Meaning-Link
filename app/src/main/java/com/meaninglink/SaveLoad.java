package com.meaninglink;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

class SaveLoad {
    ArrayList<Note> notes;
    LinkedHashMap<String,Word> dict;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson;

    SaveLoad(Context context) {
        sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        gson = new Gson();
        loadNotes();
        loadDictionary();
    }

    void save(ArrayList<Note> notes) {
        editor = sharedPreferences.edit();
        String strNotes = gson.toJson(notes);
        editor.putString("note", strNotes);
        editor.apply();
    }

    void loadNotes() {
        String strNotes = sharedPreferences.getString("note", "");
        if(!strNotes.equals("")) {
            Type type = new TypeToken<ArrayList<Note>>(){}.getType();
            notes =  gson.fromJson(strNotes, type);
        }
        else {
            notes = new ArrayList<>();
        }
    }

    int countNote() {
        return notes.size();
    }

    void add(Note note) {
        note.setDate(getDate());
        note.setTime(getTime());
        notes.add(0, note);
        save(notes);
    }

    void remove(String key) {
        Iterator<Note> iterator = notes.iterator();
        while(iterator.hasNext()) {
            if(iterator.next().getKey().equals(key)) {
                iterator.remove();
                save(notes);
            }
        }
    }

    boolean contains(Note note) {
        for(Note i : notes) {
            if(note.equals(i)) {
                return true;
            }
        }
        return false;
    }

    ArrayList<Note> getNotes() {
        loadNotes();
        return notes;
    }

    void save(LinkedHashMap<String,Word> dict) {
        editor = sharedPreferences.edit();
        String strDict = gson.toJson(dict);
        editor.putString("dict", strDict);
        editor.apply();
    }

    void loadDictionary() {
        String strDict = sharedPreferences.getString("dict", "");
        if(!strDict.equals("")) {
            Type type = new TypeToken<LinkedHashMap<String,Word>>(){}.getType();
            dict = gson.fromJson(strDict, type);
        }
        else {
            dict = new LinkedHashMap<>();
        }
    }

    void add(String word, Word data) {
        //dict = new LinkedHashMap<>();
        dict.put(word, data);
        save(dict);
    }

    boolean contains(String word) {
        return dict.containsKey(word);
    }

    Word getWord(String word) {
        if(dict.containsKey(word)) {
            return dict.get(word);
        }
        return null;
    }

    String getDate() {
        DateFormat df = new SimpleDateFormat("dd MMM yy");
        return df.format(new Date());
    }

    String getTime() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(new Date());
    }

}
