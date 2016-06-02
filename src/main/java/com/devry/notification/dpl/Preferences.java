package com.devry.notification.dpl;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

import io.vertx.core.json.JsonObject;

import static com.sleepycat.persist.model.Relationship.*;

@Entity
public class Preferences {

    @PrimaryKey
    private String deviceId;    // Mobile device id
    private String studentId;   // DSI number
    
    @SecondaryKey(relate = ONE_TO_ONE)
    private String token;       // GCM registration token
    private String platform;
    private String categories;

    public Preferences() {}
    
    public Preferences(String key, String dsi, String platform, String token, String categories) {
        this.deviceId = key;
        this.studentId = dsi;
        this.platform = platform;
        this.token = token;
        this.categories = categories;
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
        obj.put("deviceId", deviceId).put("studentId", studentId).put("platform", platform).put("token", token).put("categories", categories);
        
        return obj.toString();
    }
}
