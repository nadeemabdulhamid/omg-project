/* 
    OMG Project
    Copyright (c) 2025 Nadeem Abdul Hamid
    License: MIT
 */
package omg.handlers;

import java.io.*;
import com.sun.net.httpserver.*;

/**
 * Handles the default API endpoint, which serves a 404 page.
 */
public class DefaultHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        //String path = t.getRequestURI().getPath();
        //if (path.equals("/static")) { fileServeHandler.handle(t); return; }
        //if (path.equals("/exit")) { exitHandler.handle(t); return; }

        Headers headers = t.getResponseHeaders();
        headers.set("Content-Type", "text/html");

        String response = "File not found. <a href=\"/static/\">OMG</a>" + 
                "<p><a href=\"/exit\" onclick=\"fetch('/exit'); return false;\">Exit</a>";
        t.sendResponseHeaders(404, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}