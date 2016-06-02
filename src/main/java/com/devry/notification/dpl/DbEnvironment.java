package com.devry.notification.dpl;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

public class DbEnvironment {

    private Environment environment;
    private EntityStore store;
    
    public DbEnvironment() {}
    
    public void setup(File envHome) throws DatabaseException {
        
        EnvironmentConfig envConfig = new EnvironmentConfig();
        StoreConfig storeConfig = new StoreConfig();

        envConfig.setAllowCreate(true);
        storeConfig.setAllowCreate(true);
        
        environment = new Environment(envHome, envConfig);
        store = new EntityStore(environment, "EntityStore", storeConfig);        
    }
    
    public EntityStore getEntityStore() {
        return store;
    }
    
    public Environment getEnvironment() {
        return environment;
    }
    
    public void close() {
        if (store != null) {
            store.close();
        }
        
        if (environment != null) {
            environment.close();
        }
    }
    
}
