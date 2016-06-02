package com.devry.notification.db;

import io.vertx.core.json.JsonObject;

public class Preferences {

    private String deviceId;         // Mobile device id
    private String studentId;         // DSI number
    private String token;       // GCM registration token
    private String platform;
    private String categories;
    
    public Preferences(String key, String dsi, String platform, String token, String categories) {
        this.deviceId = key;
        this.studentId = dsi;
        this.platform = platform;
        this.token = token;
        this.categories = categories;
    }
    
    public Preferences() {
        
    }
    
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String toString() {
        JsonObject obj = new JsonObject();
        obj.put("deviceId", deviceId).put("studentId", studentId).put("platform", platform).put("token", token).put("topics", categories);
        
        return obj.toString();
    }
}
