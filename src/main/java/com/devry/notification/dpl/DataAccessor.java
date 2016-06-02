package com.devry.notification.dpl;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

public class DataAccessor {

    PrimaryIndex<String, Preferences> primaryIndex;
    SecondaryIndex<String, String, Preferences> secondaryIndex;

    public DataAccessor(EntityStore store) throws DatabaseException {
     
        primaryIndex = store.getPrimaryIndex(String.class, Preferences.class);
        secondaryIndex = store.getSecondaryIndex(primaryIndex, String.class, "token");
    }

    public PrimaryIndex<String, Preferences> getPrimaryIndex() {
        return primaryIndex;
    }

    public SecondaryIndex<String, String, Preferences> getSecondaryIndex() {
        return secondaryIndex;
    }
    
    public void put(Preferences entity) {
        primaryIndex.put(entity);
    }
}
