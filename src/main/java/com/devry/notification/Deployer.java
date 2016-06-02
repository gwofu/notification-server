/**
 * Deployer.java
 * 
 * Deploy Vert.x verticle with configuration set in configuration.json.
 * 
 * @author Gwowen Fu
 *
 */
package com.devry.notification;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class Deployer extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(Deployer.class);

    @Override
    public void start() throws Exception {
        logger.debug("Main verticle has started, let's deploy some others...");

        int port = config().getInteger("http.port", 8543);
        int instnaces = config().getInteger("total.instances", 1);
        boolean ha = config().getBoolean("high.availability", false);

        logger.debug("port=" + port);
        logger.debug("instnaces=" + instnaces);
        logger.debug("ha=" + ha);
        
        vertx.deployVerticle("com.devry.notification.NotificationVerticle", 
            new DeploymentOptions().setInstances(instnaces).setHa(ha).setConfig(
                    new JsonObject().put("http.port", port)
                    .put("keystore.path", config().getString("keystore.path", "keystore.jks"))
                    .put("keystore.pass", config().getString("keystore.pass", "password"))
                    .put("db.path", config().getString("db.path", "dbfile"))
                    .put("fcm.api.url", config().getString("fcm.api.url", ""))
                    .put("fcm.api.key", config().getString("fcm.api.key", ""))
                    .put("client.secret", config().getString("client.secret", ""))
                ));
    }
    
}