/* 
    OMG Project
    Copyright (c) 2025 Nadeem Abdul Hamid
    License: MIT
 */
package omg.handlers;

import java.io.*;
import com.sun.net.httpserver.*;

/**
 * Handles the /static API endpoint, which serves static files from the classpath.
 */
public class FileServeHandler implements HttpHandler {
    private HttpHandler defaultHandler;

    public FileServeHandler(HttpHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        Headers headers = t.getResponseHeaders();
        
        String path = t.getRequestURI().getPath().substring(1);  // clear the "/" at the beginning

        if (path.equals("favicon.ico")) { path = "static/favicon.ico"; }
        if (path.equals("static/") || path.equals("static")) { path = "static/index.html"; }

        if (!path.contains("..")) {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(path);
            if (in == null) { 
                defaultHandler.handle(t);
                return;
            }
            System.out.println("Serving: " + path);

            if (path.endsWith(".html")) { headers.set("Content-Type", "text/html"); }
            if (path.endsWith(".js")) { headers.set("Content-Type", "application/javascript"); }
            if (path.endsWith(".json")) { headers.set("Content-Type", "text/json"); }
            t.sendResponseHeaders(200, 0);

            OutputStream os = t.getResponseBody();
            Utils.copyStream(in, os);
            os.close();
        } else {
            defaultHandler.handle(t);
            return;
        }
    }
}