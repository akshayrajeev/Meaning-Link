package com.meaninglink;

class Note {
    String input;
    String key;
    String date;
    String time;

    Note(String key, String input) {
        this.key = key;
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    public String getKey() {
        return key;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean equals(Note note) {
        return this.key.equals(note.key) && this.input.equals(note.input);
    }
}
