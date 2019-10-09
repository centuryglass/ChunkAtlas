/**
 * @file Connection.java
 * 
 * Handles data transmission between the Minecraft server plugin and the web
 * viewer.
 */
package com.centuryglass.mcmap.webserver;

import com.centuryglass.mcmap.config.WebServerConfig;
import com.centuryglass.mcmap.util.JarResource;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import org.apache.commons.lang.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

/**
 * Handles data transmission between the Minecraft server plugin and the web
 * viewer.
 */
public class Connection
{
    /**
     * Configures the connection using settings from a configuration file.
     * 
     * @param webOptions  The configuration object used to set the connection's
     *                    address and port.
     */
    public Connection(WebServerConfig webOptions)
    {
        Validate.notNull(webOptions,
                "Web configuration object cannot be null.");
        client = HttpClients.createDefault();
        serverAddress = webOptions.getServerAddress() + ":"
                + String.valueOf(webOptions.getServerPort());
    }
    
    /**
     * Sends JSON data over the connection, returning any response.
     * 
     * @param messageData        A JSON object or array to send.
     * 
     * @param connectionSubPath  An optional subdirectory under the main
     *                           connection path where the request should be
     *                           sent.
     * 
     * @return                   The response received, or null if no response
     *                           arrived or the response wasn't JSON.
     */
    public JsonStructure sendJson(JsonStructure messageData,
            String connectionSubPath)
    {
        Validate.notNull(messageData, "JSON message data cannot be null.");
        
        // JSON response container, editable within lambdas:
        class MutableJson
        {
            public JsonStructure value;
            public MutableJson(JsonStructure v) { value = v; }
        }
        final MutableJson jsonResponse = new MutableJson(null);
        
        // Send JSON, saving response:
        sendMessage((post) ->
        {
            StringEntity jsonString = new StringEntity(messageData.toString(),
                    ContentType.APPLICATION_JSON);
            post.setEntity(jsonString);
        }, (response) ->
        {
            try
            {
                HttpEntity responseData = response.getEntity();
                JsonReader reader = Json.createReader(
                        responseData.getContent());
                jsonResponse.value = reader.read();
            }
            catch (IOException ex)
            {
                System.err.println("Failed to read response content: "
                        + ex.getMessage());
            }
            catch (JsonException ex)
            {
                System.err.println("Response was not valid JSON data: "
                        + ex.getMessage());
            }
            
        }, connectionSubPath);
        return jsonResponse.value;
    }
    
    /**
     * Sends a PNG image file through the connection.
     * 
     * @param imagePath          The path to an image file in the system, or
     *                           within this application's .jar resources.
     * 
     * @param headerStrings      An optional set of key/value pairs to add to
     *                           the request's header.
     * 
     * @param connectionSubPath  An optional subdirectory under the main
     *                           connection path where the request should be
     *                           sent.
     * 
     * @return                   The response status code received over the
     *                           connection, or -1 if no response was received.
     */
    public int sendPng(String imagePath, Map<String, String> headerStrings,
            String connectionSubPath)
    {
        Validate.notNull(imagePath, "Image path cannot be null.");
        // Locate the file, copying resources to temp storage if needed:
        final File imageFile;
        final File savedFile = new File(imagePath);
        if (! savedFile.isFile()) // Check if the path is to a resource:
        {
            try
            {
                imageFile = File.createTempFile("imgResource", ".png");
                JarResource.copyResourceToFile(imagePath, imageFile);
            }
            catch (IOException e)
            {
                System.err.println("Error creating temporary resource file: "
                        + e.getMessage());
                return -1;
            }
        }
        else
        {
            imageFile = savedFile;
        }
        
        // Response code container, editable within lambdas:
        class MutableInt
        {
            public int value;
            public MutableInt(int v) { value = v; }
        }
        final MutableInt responseCode = new MutableInt(-1);
        
        // Send message, declaring lambda initializer and response handler:
        sendMessage((post) -> 
        {        
            FileEntity imageEntity = new FileEntity(imageFile,
                    ContentType.IMAGE_PNG);
            post.setEntity(imageEntity);
            if (headerStrings != null)
            {
                for (Map.Entry<String, String> entry : headerStrings.entrySet())
                {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
        }, (response) ->
        {
            StatusLine status = response.getStatusLine();
            if (status != null)
            {
                responseCode.value = status.getStatusCode();
            }
        }, connectionSubPath);
        return responseCode.value;
    }
    
    /**
     * Sends a POST message through the connection.
     * 
     * @param messageInit        A function that will be applied to the message
     *                           object to initialize it.
     * 
     * @param responseHandler    A function that will be passed the response
     *                           message received from the server, if non-null.
     * 
     * @param connectionSubPath  An optional subdirectory under the main
     *                           connection path where the request should be
     *                           sent.
     */
    private void sendMessage(Consumer<HttpPost> messageInit,
            Consumer<HttpResponse> responseHandler, String connectionSubPath)
    {
        Validate.notNull(messageInit, "Message initializer cannot be null.");
        Validate.notNull(responseHandler, "Response handler cannot be null.");
        String postAddress = serverAddress;
        if (connectionSubPath != null && ! connectionSubPath.isEmpty())
        {
            if (! postAddress.endsWith("/"))
            {
                postAddress += "/";
            }
            postAddress += connectionSubPath;
        }
        HttpPost post = new HttpPost(postAddress);
        messageInit.accept(post);
        try
        {
            HttpResponse response = client.execute(post);
            if (response != null)
            {
                responseHandler.accept(response);
            }
        } 
        catch (IOException ex)
        {
            System.err.println("Error sending/receiving data: "
                    + ex.getMessage());
        }
        finally
        {
            post.releaseConnection();
        }     
    }
    
    private final HttpClient client;
    private final String serverAddress;
}
