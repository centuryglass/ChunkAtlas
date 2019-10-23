/**
 * @file Connection.java
 * 
 * Handles data transmission between the Minecraft server plugin and the web
 * viewer.
 */
package com.centuryglass.chunk_atlas.webserver;

import com.centuryglass.chunk_atlas.config.WebServerConfig;
import com.centuryglass.chunk_atlas.webserver.security.AESGenerator;
import com.centuryglass.chunk_atlas.webserver.security.KeySet;
import com.centuryglass.chunk_atlas.webserver.security.SecuredAESKey;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Base64;
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
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;

/**
 * Handles data transmission between the Minecraft server plugin and the web
 * viewer.
 */
public class Connection
{
    // Stores HTML header key strings.
    private static class HTMLHeaderKeys
    {
        // Holds the signed and encrypted AES key:
        public static final String SIGNED_AES_KEY = "key";
        // Holds the message signature in responses from the web server:
        public static final String RESPONSE_SIGNATURE = "signature";
    }
    
    private static final int MAX_IMAGE_SIZE = 1000000;
    
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
        this.webOptions = webOptions;
        client = HttpClients.createDefault();
        serverAddress = webOptions.getServerAddress() + ":"
                + String.valueOf(webOptions.getServerPort());
    }
    
    /**
     * Adds a secured AES key and encrypted message body to a POST request
     * object.
     * 
     * @param post                        The HTTP POST object to update.
     * 
     * @param rsaKeys                     Keys used to sign and encrypt the AES
     *                                    key.
     * 
     * @param message                     Message data that should be encrypted
     *                                    and set as the POST request's main
     *                                    data.
     * 
     * @throws GeneralSecurityException   If unable to properly sign or encrypt
     *                                    the message.
     */
    private void addSignedEncryptedMessage(HttpPost post, KeySet rsaKeys,
            byte[] message) throws GeneralSecurityException
    {
        final SecuredAESKey aesKey = new SecuredAESKey(AESGenerator.generate(),
                rsaKeys);
        post.addHeader(HTMLHeaderKeys.SIGNED_AES_KEY,
                aesKey.getSecuredKeyString());
        
        byte[] encryptedMessage = aesKey.encryptMessage(message);
        ByteArrayEntity encryptedEntity
                = new ByteArrayEntity(encryptedMessage);
        post.setEntity(encryptedEntity);
    }
    
    /**
     * Creates a KeySet using the RSA key files defined in web configuration
     * options.
     * 
     * @return               The object providing RSA keys used to securely
     *                       connect to the web server.
     * 
     * @throws IOException   If unable to read any of the key files. 
     */
    private KeySet loadRSAKeys() throws IOException
    {
        return new KeySet(webOptions.getPrivateKeyFile(),
                    webOptions.getPublicKeyFile(),
                    webOptions.getWebPublicKeyFile());
    }
    
    /**
     * Sends JSON data over the connection, returning any response.
     * 
     * @param messageData                A JSON object or array to send.
     * 
     * @param connectionSubPath          An optional subdirectory under the main
     *                                   connection path where the request
     *                                   should be sent.
     * 
     * @return                           The response received, or null if no
     *                                   response arrived or the response wasn't
     *                                   JSON.
     *
     * @throws IOException               If unable to load encryption keys.
     * 
     * @throws GeneralSecurityException  If unable to create a secured
     *                                   encryption key to share with the 
     *                                   intended recipient.
     */
    public JsonStructure sendJson(JsonStructure messageData,
            String connectionSubPath) throws IOException,
            GeneralSecurityException
    {
        Validate.notNull(messageData, "JSON message data cannot be null.");
        
        // JSON response container, editable within lambdas:
        class MutableJson
        {
            public JsonStructure value;
            public MutableJson(JsonStructure v) { value = v; }
        }
        final MutableJson jsonResponse = new MutableJson(null);
        final KeySet rsaKeys = loadRSAKeys();
        // Sign, encrypt, and send JSON data:
        sendMessage((post) ->
        {
            String messageStr = messageData.toString();
            byte[] byteMessage = messageStr.getBytes(
                    Charset.forName("UTF-8"));
            try
            {
                addSignedEncryptedMessage(post, rsaKeys, byteMessage);
            }
            catch (GeneralSecurityException e)
            {
                System.out.println("Failed to sign and encrypt message data: "
                        + e.toString());
            }
        }, (response) ->
        {
            try
            {
                if (! response.containsHeader(
                        HTMLHeaderKeys.RESPONSE_SIGNATURE))
                {
                    System.err.println("Server response was not signed!");
                    return;
                }
                HttpEntity responseData = response.getEntity();
                InputStream bodyStream = responseData.getContent();
                int bodySize = (int) responseData.getContentLength();
                byte [] body = new byte[bodySize];
                int lastReadSize = bodyStream.read(body);
                int bytesRead = lastReadSize;
                while (bytesRead < bodySize && lastReadSize > 0)
                {
                    lastReadSize = bodyStream.read(body, bytesRead,
                            bodySize - bytesRead);
                    bytesRead += lastReadSize;
                }
                Validate.isTrue(bytesRead == bodySize,
                        "Failed to read entire response message."
                        + " Expected: " + String.valueOf(bodySize)
                        + ", found: " + String.valueOf(bytesRead));
                byte [] signature = Base64.getDecoder().decode(
                        response.getFirstHeader(
                        HTMLHeaderKeys.RESPONSE_SIGNATURE).getValue());
                
                System.out.println("Checking "
                        + String.valueOf(signature.length)
                        + "-byte signature against "
                        + String.valueOf(body.length)
                        + "-byte response body...");
                boolean isValidResponse;
                try
                {
                    isValidResponse = rsaKeys.verifyRemoteSignedMessage(
                            signature, body);
                }
                catch (InvalidKeyException | SignatureException e)
                {
                    System.err.println(e.toString());
                    isValidResponse = false;
                }
                   
                if (isValidResponse)
                {
                    ByteArrayInputStream jsonStream
                            = new ByteArrayInputStream(body);
                    JsonReader reader = Json.createReader(jsonStream);
                    jsonResponse.value = reader.read();
                }
                else
                {
                    System.err.println("Update response lacked a valid "
                            + "signature, and will be ignored.");
                }
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
     *
     * @throws IOException       If unable to read the image from the given
     *                           path.
     */
    public int sendPng(String imagePath, Map<String, String> headerStrings,
            String connectionSubPath) throws IOException
    {
        Validate.notNull(imagePath, "Image path cannot be null.");
        // Locate the file, copying resources to temp storage if needed:
        final File imageFile;
        final File savedFile = new File(imagePath);
        InputStream imageStream;
        if (! savedFile.isFile()) // Check if the path is to a resource:
        {
            imageStream = Connection.class.getResourceAsStream(imagePath);
        }
        else
        {
            try 
            {
                imageStream = new FileInputStream(savedFile);
            }
            catch (FileNotFoundException e)
            {
                imageStream = null;
            }
            imageFile = savedFile;
        }
        if (imageStream == null)
        {
            throw new IOException("Unable to find image \"" + imagePath + "\"");
        }
        byte[] imageBuffer = new byte[MAX_IMAGE_SIZE];
        int lastRead = imageStream.read(imageBuffer);
        int bytesRead = lastRead;
        while (bytesRead < MAX_IMAGE_SIZE && lastRead > 0)
        {
            lastRead = imageStream.read(imageBuffer, bytesRead,
                    MAX_IMAGE_SIZE - bytesRead);
            bytesRead += lastRead;
        }
        byte[] imageData = Arrays.copyOfRange(imageBuffer, 0, bytesRead);
        
        // Response code container, editable within lambdas:
        class MutableInt
        {
            public int value;
            public MutableInt(int v) { value = v; }
        }
        final MutableInt responseCode = new MutableInt(-1);
        final KeySet rsaKeys = loadRSAKeys();
        
        // Send message, declaring lambda initializer and response handler:
        sendMessage((post) -> 
        {        
            try
            {
                addSignedEncryptedMessage(post, rsaKeys, imageData);
            }
            catch (GeneralSecurityException e)
            {
                System.out.println("Failed to sign and encrypt message data: "
                        + e.toString());
            }
            if (headerStrings != null)
            {
                for (Map.Entry<String, String> pair : headerStrings.entrySet())
                {
                    post.addHeader(pair.getKey(), pair.getValue());
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
    private final WebServerConfig webOptions;
}
