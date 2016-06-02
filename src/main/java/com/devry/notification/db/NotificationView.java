package com.devry.notification.db;

import com.sleepycat.bind.EntityBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.SerialSerialBinding;
import com.sleepycat.collections.StoredEntrySet;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.collections.StoredValueSet;

public class NotificationView {
    private StoredSortedMap<PreferencesKey, Preferences> preferenceMap;
    
    public NotificationView(NotificationDB db) {
        ClassCatalog catalog = db.getClassCatalog();
        SerialBinding preferencesKeyBinding = new SerialBinding(catalog, PreferencesKey.class);
        EntityBinding preferencesDataBinding = new PreferenceBinding(catalog, PreferencesKey.class, PreferencesData.class);
        
        preferenceMap = new StoredSortedMap(db.getPreferenceDb(), preferencesKeyBinding, preferencesDataBinding, true);
    }
    
    public final StoredSortedMap<PreferencesKey, Preferences> getPreferenceMap() {
        return preferenceMap;
    }
    
    public final StoredEntrySet getPreferenceEntrySet() {
        return (StoredEntrySet) preferenceMap.entrySet();
    }

    public StoredValueSet getPreferenceSet() {
        return (StoredValueSet) preferenceMap.values();
    }
    
    private static class PreferenceBinding extends SerialSerialBinding {

        @SuppressWarnings("unchecked")
        public PreferenceBinding(ClassCatalog classCatalog, Class<PreferencesKey> keyClass, Class<PreferencesData> dataClass) {
            super(classCatalog, keyClass, dataClass);
        }

        @Override
        public Preferences entryToObject(Object keyInput, Object dataInput) {
            PreferencesKey key = (PreferencesKey) keyInput;
            PreferencesData data = (PreferencesData) dataInput;
            
            return new Preferences(key.getKey(), data.getDsi(), data.getPlatform(), data.getToken(), data.getCategories());
        }

        @Override
        public PreferencesData objectToData(Object object) {
            Preferences preferences = (Preferences) object;
            return new PreferencesData(
                    preferences.getStudentId(),
                    preferences.getPlatform(),
                    preferences.getToken(),
                    preferences.getCategories());
        }

        @Override
        public PreferencesKey objectToKey(Object object) {
            Preferences preference = (Preferences) object;
            return new PreferencesKey(preference.getDeviceId());
        }
        
    }
}
