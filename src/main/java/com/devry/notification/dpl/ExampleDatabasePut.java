package com.devry.notification.dpl;

import java.io.File;

import com.sleepycat.persist.EntityCursor;

public class ExampleDatabasePut {

    private static File envHome = new File("./JEDB");

    private DataAccessor da;
    private static DbEnvironment environment = new DbEnvironment();
   
    public void run() {
        environment.setup(envHome);
        da = new DataAccessor(environment.getEntityStore());
        
        //saveData();
        
        readData();
    }
    
    private void saveData() {
        Preferences pref = new Preferences();
        pref.setDeviceId("deviceId-02");
        pref.setStudentId("D00000002");
        pref.setToken("token-02");
        pref.setPlatform("android");
        pref.setCategories("0000000000000002");
        
        da.primaryIndex.put(pref);
        
        Preferences preferences = new Preferences("deviceId-03", "D00000003", "android", "token-03", "0000000000000003");
        da.primaryIndex.put(preferences);

    }

    private void readData() {
       EntityCursor<Preferences> preferences = da.secondaryIndex.subIndex("token-03").entities();
       
       for (Preferences item : preferences) {
           System.out.println("DeviceId : " + item.getDeviceId());
           System.out.println("StudentId : " + item.getStudentId());
           System.out.println("Token : " + item.getToken());
           System.out.println("Platform : " + item.getPlatform());
           System.out.println("Categories : " + item.getCategories());
       }
       
       Preferences p = da.getPrimaryIndex().get("deviceId-02");
       System.out.println("DeviceId : " + p.getDeviceId());
       System.out.println("StudentId : " + p.getStudentId());
       System.out.println("Token : " + p.getToken());
       System.out.println("Platform : " + p.getPlatform());
       System.out.println("Categories : " + p.getCategories());
       
       preferences.close();
    }

    public static void main(String args[]) {
        ExampleDatabasePut edp = new ExampleDatabasePut();
        
        try {
            edp.run();
        } finally {
            environment.close();
        }
        
    }
}
