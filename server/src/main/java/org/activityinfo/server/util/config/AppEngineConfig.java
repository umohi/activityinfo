package org.activityinfo.server.util.config;

import com.google.appengine.api.datastore.*;

/**
 * Utility class for retrieving / storing properties files from the AppEngine
 * datastore.
 * <p/>
 * <p/>
 * We just store the text of the configuration file to a single key in the
 * datastore.
 */
public class AppEngineConfig {

    public static String getPropertyFile() {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        Entity entity;
        try {
            entity = datastore.get(key());
        } catch (EntityNotFoundException e) {
            return "";
        }
        Text text = (Text) entity.getProperty("text");
        return text.getValue();
    }

    public static void setPropertyFile(String string) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        Entity entity = new Entity(key());
        entity.setProperty("text", new Text(string));
        datastore.put(entity);
    }

    public static Key key() {
        return KeyFactory.createKey("Configuration", "config");
    }

}
