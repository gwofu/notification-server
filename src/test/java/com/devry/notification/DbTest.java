package com.devry.notification;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.devry.notification.dpl.DataAccessor;
import com.devry.notification.dpl.DbEnvironment;
import com.devry.notification.dpl.Preferences;
import com.sleepycat.persist.PrimaryIndex;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class DbTest {

    private static final Logger logger = LoggerFactory.getLogger(DbTest.class);

    // DPL properties
    private static DbEnvironment dbEnvironment = new DbEnvironment();
    private DataAccessor dataAccessor;
    private Random randomGenerator = new Random();

    public DbTest() {
        
    }
    
    public void setup() {
        String path = "./dbfiletest";
        File dbPath = new File(path);
        if(!dbPath.exists()) {
            dbPath.mkdirs();
        } 

        dbEnvironment.setup(dbPath);
        dataAccessor = new DataAccessor(dbEnvironment.getEntityStore());
    }
    
    public void destroy() {
        dbEnvironment.close();
    }
    
    public void createData(int size) {
        Preferences preferences = null;
        String platform = null;
        String categories = null;
        PrimaryIndex<String, Preferences> primaryIndex = dataAccessor.getPrimaryIndex();

        System.out.println("Before count= " + primaryIndex.count());

        for (int i=0; i<size; i++) {
            platform = randomGenerator.nextInt(2) == 0 ? "android" : "ios";
            categories = Integer.toBinaryString(randomGenerator.nextInt(i+1));
            preferences = new Preferences("deviceId" + i, "studentId" + i, platform, "token" + i, categories);
            logger.debug(preferences.toString());
            primaryIndex.put(preferences);            
        }
        
        System.out.println("After count= " + primaryIndex.count());
    }

    public void readData(int size) {
        PrimaryIndex<String, Preferences> primaryIndex = dataAccessor.getPrimaryIndex();

        System.out.println("Before count= " + primaryIndex.count());
        Preferences preferences = null;

        for (int i=0; i<size; i++) {
            preferences = primaryIndex.get("deviceId" + i);
            System.out.println(preferences.toString());
        }
        
        System.out.println("After count= " + primaryIndex.count());
    }

    public static void main(String[] args) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        DbTest dbTest = new DbTest();
        
        dbTest.setup();
        System.out.println(dateFormat.format(new Date()));

        dbTest.createData(100000);
        System.out.println(dateFormat.format(new Date()));

        dbTest.readData(10000);
        System.out.println(dateFormat.format(new Date()));
        
        dbTest.destroy();
        System.out.println(dateFormat.format(new Date()));

    }
}
