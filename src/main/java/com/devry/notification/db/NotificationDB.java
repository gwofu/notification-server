package com.devry.notification.db;

import java.io.File;
import java.io.FileNotFoundException;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class NotificationDB {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationDB.class);

    private Environment env;
    private static final String CLASS_CATALOG = "java_class_catalog";
    private StoredClassCatalog javaCatalog;
    
    private static final String PREFERENCE_STORE = "preference_store";
    private Database preferenceDb;

    public NotificationDB(String homeDirectory) 
            throws DatabaseException, FileNotFoundException {
        
        logger.debug("Opening environment in: " + homeDirectory);
        
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        envConfig.setTransactional(true);       
        env = new Environment(new File(homeDirectory), envConfig);
        
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setTransactional(true);
        Database catalogDB = env.openDatabase(null, CLASS_CATALOG, dbConfig);
        preferenceDb = env.openDatabase(null, PREFERENCE_STORE, dbConfig);
        
        javaCatalog = new StoredClassCatalog(catalogDB);
    }
    
    public void close() throws DatabaseException {
        preferenceDb.close();
        javaCatalog.close();
        env.cleanLog();
        env.close();
    }

    public final Environment getEnvironment() {
        return env;
    }
    
    public final StoredClassCatalog getClassCatalog() {
        return javaCatalog;
    }
    
    public final Database getPreferenceDb() {
        return preferenceDb;
    }
}
