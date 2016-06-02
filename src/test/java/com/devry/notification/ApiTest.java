package com.devry.notification;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.devry.notification.dpl.Preferences;

import guru.nidi.ramltester.RamlDefinition;
import guru.nidi.ramltester.RamlLoaders;
import guru.nidi.ramltester.core.RamlReport;
import guru.nidi.ramltester.jaxrs.CheckingWebTarget;
import guru.nidi.ramltester.junit.RamlMatchers;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ApiTest {
    
    private static Vertx vertx;
    
    private static final RamlDefinition api = RamlLoaders.fromClasspath()
            .load("webroot/api/api.raml")
            .assumingBaseUri("https://localhost:8543/");
    
    private static ResteasyClient client = new ResteasyClientBuilder().build();
    private static CheckingWebTarget webTarget;

    private static JsonObject jsonObj;
    
    @BeforeClass
    public static void setUp(TestContext context) {
        jsonObj = new JsonObject().put("http.port", 8543)
                .put("db.path", "./dbfile")
                .put("fcm.api.url", "https://fcm.googleapis.com/fcm/send")
                .put("fcm.api.key", "AIzaSyDcpbe9XYTh8wsoA9LzCW4vMW176KnMY1w")
                .put("client.secret", "cG9ydGFsdGVhbTpEZXZyeTEyMw==")
                .put("test", true);
        
        DeploymentOptions options = new DeploymentOptions().setConfig(jsonObj);
            
        vertx = Vertx.vertx();
        vertx.deployVerticle(NotificationVerticle.class.getName(), options, context.asyncAssertSuccess());
        
        webTarget = api.createWebTarget(client.target("http://localhost:8543"));
    }
    
    @AfterClass
    public static void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }
    
    @Test
    public void test1() {
        webTarget.path("/api/version").request().get();
        RamlReport report = webTarget.getLastReport();
        Assert.assertThat(report, RamlMatchers.hasNoViolations());
    }

    @Test
    public void testPreferencesGet() {
        webTarget.path("/api/preferences").request()
            .get();
        RamlReport report = webTarget.getLastReport();
        Assert.assertThat(report, RamlMatchers.hasNoViolations());
    }

    @Test
    public void testPreferencesGetByDeviceId() {
        webTarget.path("/api/preferences/deviceid").request()
            .get();
        RamlReport report = webTarget.getLastReport();
        Assert.assertThat(report, RamlMatchers.hasNoViolations());
    }
    
    @Test
    public void testPreferencesPost(TestContext context) {

        Preferences preferences = new Preferences("deviceId", "studentId", "platform", "token", "categories");

        JsonObject jsonObj = new JsonObject(preferences.toString());
        
        final Async async = context.async();
        HttpClientOptions options = new HttpClientOptions();
        options.setDefaultPort(8543);
        vertx.createHttpClient(options).post("/api/preferences", response -> {
            response.handler(body -> {
               System.out.println("body=" + body);
               Assert.assertTrue(body.toJsonObject().getValue("deviceId").equals("deviceId"));
               async.complete(); 
            });
        })
        .putHeader("content-length", String.valueOf(jsonObj.toString().length()))
        .write(jsonObj.toString())
        .end();
    }
    
    @Test
    public void testNotificationPost(TestContext context) {

        JsonObject jsonObj = new JsonObject();
        jsonObj.put("to", "/topics/news");
        jsonObj.put("notification", new JsonObject("{\"title\": \"FCM News\", \"body\": \"News\", \"icon\": \"myicon\"}"));
        
        final Async async = context.async();
        HttpClientOptions options = new HttpClientOptions();
        options.setDefaultPort(8543);
        vertx.createHttpClient(options).post("/api/notification", response -> {
            response.handler(body -> {
               System.out.println("body=" + body);
               Assert.assertTrue(body.toString().contains("message_id"));     
               async.complete(); 
            });
        })
        .putHeader("content-length", String.valueOf(jsonObj.toString().length()))
        .write(jsonObj.toString())
        .end();
    }    
}
