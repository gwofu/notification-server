package com.devry.notification.db;

import java.io.Serializable;

public class PreferencesKey implements Serializable {
    private static final long serialVersionUID = 1L;
    private String key;

    public PreferencesKey(String key) {
        this.key = key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String toString() {
        return "[PreferencesKey: key=" + key + "]";
    }
    
}
