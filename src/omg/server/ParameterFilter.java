// Adapted from:
// https://leonardom.wordpress.com/2009/08/06/getting-parameters-from-httpexchange/
package omg.server;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ParameterFilter extends Filter {

    @Override
    public String description() {
        return "Parses the requested URI for parameters";
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain)
        throws IOException {
        parseGetParameters(exchange);
        parsePostParameters(exchange);
        chain.doFilter(exchange);
    }    

    private void parseGetParameters(HttpExchange exchange)
        throws UnsupportedEncodingException {

        Map<String, Object> parameters = new HashMap<String, Object>();
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        parseQuery(query, parameters);
        exchange.setAttribute("parameters", parameters);
    }

    private void parsePostParameters(HttpExchange exchange)
        throws IOException {

        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            @SuppressWarnings("unchecked")
            Map<String, Object> parameters =
                (Map<String, Object>)exchange.getAttribute("parameters");
            InputStreamReader isr =
                new InputStreamReader(exchange.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            parseQuery(query, parameters);
        }
    }

     @SuppressWarnings("unchecked")
     private void parseQuery(String query, Map<String, Object> parameters)
         throws UnsupportedEncodingException {

         if (query != null) {
             String pairs[] = query.split("[&]");

             for (String pair : pairs) {
                 String param[] = pair.split("[=]");

                 String key = null;
                 String value = null;
                 if (param.length > 0) {
                     key = URLDecoder.decode(param[0],
                         System.getProperty("file.encoding"));
                 }

                 if (key.length() > 0) {
	                 if (param.length > 1) {
	                     value = URLDecoder.decode(param[1],
	                         System.getProperty("file.encoding"));
	                 }
	
	                 if (parameters.containsKey(key)) {
	                     Object obj = parameters.get(key);
	                     if(obj instanceof List<?>) {
	                         List<String> values = (List<String>)obj;
	                         values.add(value);
	                     } else if(obj instanceof String) {
	                         List<String> values = new ArrayList<String>();
	                         values.add((String)obj);
	                         values.add(value);
	                         parameters.put(key, values);
	                     }
	                 } else {
	                     parameters.put(key, value);
	                 }
                 }
             }
         }
    }
}
