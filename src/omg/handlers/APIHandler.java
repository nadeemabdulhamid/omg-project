/* 
    OMG Project
    Copyright (c) 2025 Nadeem Abdul Hamid
    License: MIT
 */
package omg.handlers;

import java.io.*;
import java.util.*;
import com.sun.net.httpserver.*;
import omg.server.OMGServer;

@SuppressWarnings({ "unchecked" })
public class APIHandler implements HttpHandler {
    private final OMGServer server;

    public APIHandler(OMGServer server) {
        this.server = server;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        try {
            String[] pathpcs = t.getRequestURI().getPath().split("/");
            String service = pathpcs[2];

            Map<String, Object> params = (Map<String, Object>)t.getAttribute("parameters");
            System.out.println("API serving: " + service + " Params: " + params);

            if (!server.hasHandlerFor(service)) {
                String response = " service: " + service + " params: " + params;
                Utils.sendJSON(t, response);
            } else if (service.equals("catalog")) {
                String sortorder = (String)params.getOrDefault("sort", "");
                boolean ascending = "asc".equals((String)params.getOrDefault("order", "asc"));
                Utils.sendJSON(t, server.getHandlerFor(service).getResponse(server.constructPredicate(params),
                        sortorder, ascending));
            } else if (service.equals("item-data") || 
                    service.equals("cart-add") ||
                    service.equals("cart-remove")) {
                int id = Integer.parseInt((String)(params.get("id")));				
                Utils.sendJSON(t, server.getHandlerFor(service).getResponse(id));
            } else if (service.equals("count")) {
                Utils.sendJSON(t, server.getHandlerFor(service).getResponse(server.constructPredicate(params)));			
            } else if (service.equals("price-range") || service.equals("year-range")) {
                Utils.sendJSON(t, server.getHandlerFor(service).getResponse(server.constructPredicate(params, true)));
            } else if (service.equals("tags")) {
                String tagsearch = (String)params.getOrDefault("tag-search", "");
                int limit = Integer.parseInt((String)params.getOrDefault("tag-limit", "-1"));
                Utils.sendJSON(t, server.getHandlerFor(service).getResponse(server.constructPredicate(params), tagsearch, limit));
            } else if (service.equals("cart-count") 
                    || service.equals("cart-list")
                    || service.equals("cart-subtotal")
                    || service.equals("cart-total")
                    || service.equals("cart-get-coupon")) {
                        Utils.sendJSON(t, server.getHandlerFor(service).getResponse());	
            } else if (service.equals("cart-apply-coupon")
                    || service.equals("cart-remove-coupon")) {
                String code = (String)params.getOrDefault("code", "");
                Utils.sendJSON(t, server.getHandlerFor(service).getResponse(code));
            } else {
                String response = " service: " + service + " params: " + params;
                Utils.sendJSON(t, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }			
    }
}