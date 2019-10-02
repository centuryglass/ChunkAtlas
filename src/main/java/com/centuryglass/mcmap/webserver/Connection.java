/**
 * @file Connection.java
 * 
 * Handles data transmission between the Minecraft server plugin and the web
 * viewer.
 */
package com.centuryglass.mcmap.webserver;

import com.centuryglass.mcmap.config.WebServerConfig;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import org.apache.commons.lang.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

/**
 * Handles data transmission between the Minecraft server plugin and the web
 * viewer.
 */
public class Connection
{
    public Connection(WebServerConfig webOptions)
    {
        Validate.notNull(webOptions,
                "Web configuration object cannot be null.");
        client = HttpClients.createDefault();
        serverAddress = webOptions.getServerAddress() + ":"
                + String.valueOf(webOptions.getServerPort());
    }
    
    public JsonStructure sendJson(JsonStructure messageData)
    {
        Validate.notNull(messageData, "JSON message data cannot be null.");
        HttpPost post = new HttpPost(serverAddress);
        StringEntity jsonString = new StringEntity(messageData.toString(),
                ContentType.APPLICATION_JSON);
        post.setEntity(jsonString);
        try
        {
            HttpResponse response = client.execute(post);
            HttpEntity responseData = response.getEntity();
            JsonReader reader = Json.createReader(responseData.getContent());
            return reader.read();
        } 
        catch (IOException ex)
        {
            System.err.println("Failed to send JSON data to web server: "
                    + ex.getMessage());
        }
        catch (JsonException ex)
        {
            System.err.println("Response was not valid JSON data: "
                    + ex.getMessage());
        }
        finally
        {
            post.releaseConnection();
        }
        return null;      
    }
    
    private final HttpClient client;
    private final String serverAddress;
}
