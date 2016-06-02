package com.devry.notification.db;

import java.io.Serializable;

public class PreferencesData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String dsi;
    private String token;
    private String platform;
    private String categories;
       

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    
    public PreferencesData(String dsi, String platform, String token, String categories) {
        this.dsi = dsi;
        this.platform = platform;
        this.token = token;
        this.categories = categories;
    }
    
    public String getDsi() {
        return dsi;
    }

    public void setDsi(String dsi) {
        this.dsi = dsi;
    }

    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public String getCategories() {
        return categories;
    }

    public void setCategories(String topics) {
        this.categories = topics;
    }
    
    public String toString() {
        return "[PreferenceData: " +
                " dsi=" + dsi +
                " token=" + token + 
                " categories=" + categories + "]";
    }

}
