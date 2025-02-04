/* 
OMG Project
Copyright (c) 2025 Nadeem Abdul Hamid
License: MIT
*/
package omg.handlers;

import java.io.*;
import java.math.BigDecimal;

import com.sun.net.httpserver.*;

public class Utils {
    
    /**
    * Sends a JSON response to the client.
    * @param t 
    * @param response The JSON data to send
    * @throws IOException
    */
    public static void sendJSON(HttpExchange t, String response) throws IOException{
        Headers headers = t.getResponseHeaders();
        headers.set("Content-Type", "text/json");
        t.sendResponseHeaders(200, response.length());
        
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    
    
    /** 
    * Copies the contents of an InputStream to an OutputStream.
    */
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }
    
    /**
    * Converts a string representing a price in dollars to an integer representing
    * the price in cents.
    *
    * Example: extractPriceAsCents("1.23") returns 123
    *
    * @param value The price in dollars
    * @return The price in cents
    */
    public static int extractPriceAsCents(String value) {
        return new BigDecimal(value).multiply(new BigDecimal(100)).intValue();
    }
    
}
