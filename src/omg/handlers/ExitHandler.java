/* 
    OMG Project
    Copyright (c) 2025 Nadeem Abdul Hamid
    License: MIT
 */
package omg.handlers;

import java.io.IOException;
import com.sun.net.httpserver.*;

/**
 * Handles the /exit API endpoint, which shuts down the server.
 */
public class ExitHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        System.out.println("Shutting down.");
        System.exit(0);
    }
}
