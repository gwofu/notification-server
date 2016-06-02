package com.devry.notification;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.devry.notification.dpl.DataAccessor;
import com.devry.notification.dpl.DbEnvironment;
import com.devry.notification.dpl.Preferences;
import com.sleepycat.persist.EntityCursor;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class NotificationVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(NotificationVerticle.class);
    
    private static String clientSecret;
    private static HttpURLConnection fcmConn;
    private static HttpURLConnection mobileServiceConn;
    
    // DPL properties
    private static DbEnvironment dbEnvironment = new DbEnvironment();
    private DataAccessor dataAccessor;

    private Encrypter encrypter = new Encrypter();

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        logger.debug("Verticle started " + Thread.currentThread().getName());
        
        super.start(startFuture);

        clientSecret = config().getString("client.secret");

        fcmConn = openFCMConnection();
//        mobileServiceConn = openMobileServiceConnection();
        
        String path = config().getString("db.path");
        File dbPath = new File(path);
        if(!dbPath.exists()) {
            dbPath.mkdirs();
        } 
        
        dbEnvironment.setup(dbPath);
        dataAccessor = new DataAccessor(dbEnvironment.getEntityStore());
        
        Router router = Router.router(vertx);
        
        if (!config().getBoolean("test", false)) {
          router.route("/api/preferences*").handler(this::authHandler);
          router.route("/api/notification*").handler(this::authHandler);            
        }
        router.route("/api/preferences*").handler(BodyHandler.create().setBodyLimit(10240));
        router.route("/api/notification*").handler(BodyHandler.create().setBodyLimit(10240));
        router.get("/api/preferences").handler(this::getAllPreferences);
        router.get("/api/preferences/:deviceid").handler(this::getPreferences);
        router.post("/api/preferences").handler(this::postPreferences);
        router.post("/api/notification").handler(this::postNotification);
        router.get("/api/version").handler(this::getVersion);

        router.route().handler(StaticHandler.create());

        final String keystorePath = config().getString("keystore.path");
        final String keystorePass = config().getString("keystore.pass");

        HttpServerOptions options = new HttpServerOptions().setSsl(true)
                .setKeyStoreOptions(new JksOptions().setPath(keystorePath).setPassword(keystorePass));

        if (keystorePath != null) {
            vertx.createHttpServer(options).requestHandler(router::accept).listen(config().getInteger("http.port"));            
        }
        else {
            vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port"));            
        }
        
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        
        if (fcmConn != null) {
            fcmConn.disconnect();            
        }
        
        if (dbEnvironment != null) {
            dbEnvironment.close();           
        }

        super.stop(stopFuture);
    }

    /**
     * Open FCM connection
     * @throws IOException
     */
    private HttpURLConnection openFCMConnection() throws MalformedURLException, IOException {
        String apiUrl = config().getString("fcm.api.url");
        logger.debug("API URL = " + apiUrl);
        
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "key=" + config().getString("fcm.api.key"));
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        
        return conn;
    }

//    /**
//     * Open Mobile Service API connection
//     * @return
//     * @throws MalformedURLException
//     * @throws IOException
//     */
//    private HttpURLConnection openMobileServiceConnection() throws MalformedURLException, IOException {
//        String apiUrl = config().getString("mobile.service.api.url");
//        logger.debug("API URL = " + apiUrl);
//        
//        URL url = new URL(apiUrl);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestProperty("authorization", config().getString("mobile.service.authorization"));
//        conn.setRequestProperty("dsi", config().getString("mobile.service.dsi"));
//        conn.setRequestProperty("Content-Type", "application/json");
//        conn.setRequestMethod("POST");
//        conn.setDoOutput(true);
//        
//        return conn;
//    }

    /**
     * Authentication handler
     * @param routingContext
     */
    private void authHandler(RoutingContext routingContext) {
        String authorization = routingContext.request().getHeader("authorization");
        String dsi = routingContext.request().getHeader("dsi");
        
        boolean authorized = true;

        if (authorization == null || dsi == null || authorization.isEmpty() || dsi.isEmpty()) {
            authorized = false;
        } else {
            String hash = encrypter.encrypt(clientSecret, dsi);
            if (hash.compareTo(authorization) != 0) {
                authorized = false;
            }
        }
        
        if (!authorized) {
            JsonObject jsonObj = new JsonObject();
            jsonObj.put("error", "User not authorized");
            
            response(routingContext, 401, jsonObj);
        }
        else {
            routingContext.next();
        }
        
    }
    

    /**
     * Get Notification Server version
     * @param routingContext
     */
    private void getVersion(RoutingContext routingContext) {
        response(routingContext, 200, new JsonObject().put("version", "0.1"));
    }

    /**
     * Get preferences by device ID
     * @param routingContext
     */
    private void getPreferences(RoutingContext routingContext) {
        String id = routingContext.request().getParam("deviceid");
        
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
            return;
        }

        Preferences preferences = dataAccessor.getPrimaryIndex().get(id);
        
        if (preferences == null) {
            routingContext.response().setStatusCode(404).end();
            return;
        }
        
        logger.debug(preferences.toString());
        
        response(routingContext, 200, preferences);

    }

    private void postPreferences(RoutingContext routingContext) {
        String body = routingContext.getBodyAsString();
        JsonObject jsonObj = new JsonObject(body);

        String deviceId = jsonObj.getString("deviceId");
        String studentId = jsonObj.getString("studentId");
        String platform = jsonObj.getString("platform");
        String token = jsonObj.getString("token");
        String categories = jsonObj.getString("categories");
        final Preferences preferences = new Preferences(deviceId, studentId, platform, token, categories);
        logger.debug(preferences.toString());
        
        dataAccessor.getPrimaryIndex().put(preferences);
        
        response(routingContext, 201, preferences);

    }

    /**
     * Post notification to FCM
     * @param routingContext
     */
    private void postNotification(RoutingContext routingContext) {
        String body = routingContext.getBodyAsString();

        String response = "";
        
        try {
              // Send FCM message content.
            OutputStream outputStream;
            outputStream = fcmConn.getOutputStream();
            outputStream.write(body.getBytes());

            // Read FCM response.
            InputStream inputStream = fcmConn.getInputStream();
            response = IOUtils.toString(inputStream);
            logger.debug(response);

            response(routingContext, 201, response);

        } catch (IOException e) {
            System.out.println("Unable to send FCM message.");
            System.out.println("Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            logger.debug(e.getMessage());
            
            JsonObject obj = new JsonObject();
            obj.put("error", e.getMessage());
                       
            response(routingContext, 404, obj);
        }        
            
    }    
    
    /**
     * Get all preferences.
     * @param routingContext
     */
    private void getAllPreferences(RoutingContext routingContext) {
        JsonArray array = new JsonArray();
        
        EntityCursor<Preferences> ps = dataAccessor.getPrimaryIndex().entities();
        
        for (Preferences p : ps) {
          logger.debug("p=" + p);
          JsonObject obj = new JsonObject(p.toString());
          array.add(obj);            
        }
        
        ps.close();
        
        response(routingContext, 200, array);
        
    }

    /**
     * Helper method
     * @param routingContext
     * @param statusCode
     * @param data
     */
    private void response(RoutingContext routingContext, int statusCode, Object data) {
        
        logger.debug(data.toString());
        
        routingContext.response()
        .setStatusCode(statusCode)
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(data));                        
    }

}
