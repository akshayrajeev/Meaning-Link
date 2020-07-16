package com.meaninglink;

import java.util.Date;

class Note {
    Date lastModified;
    String input;
    String key;

    Note(String key, String input) {
        this.key = key;
        this.input = input;
        //this.lastModified = lastModified;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public String getInput() {
        return input;
    }

    public String getKey() {
        return key;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
