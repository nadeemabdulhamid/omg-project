/* 
OMG Project
Copyright (c) 2025 Nadeem Abdul Hamid
License: MIT
*/
package omg.server;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.Predicate;

import com.sun.net.httpserver.*;
import omg.handlers.*;
import omg.interfaces.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class OMGServer {
    private final static int DEFAULT_PORT = 8000;

    /* data file */
    private final String dataFile;

    /* fields for all the client hooks */
    // map of API request paths to request handlers
    private Map<String,RequestHandler> handlers = new HashMap<String,RequestHandler>();

    // User-specified classes to be constructed from JSON data for each item type in the
    // map ("audio", "print", or "video").
    private Map<String, DataConstructor> constrs = new HashMap<String,DataConstructor>();

    // user-specied constructors for predicates used for filtering items based on 
    // the query parameters in the API request (the parameters are the keys of the map)
    private final static Predicate defaultItemPredicate = new Predicate() { public boolean test(Object t) { return true; } };
	private Map<String,PredicateConstructor> predicates = new HashMap<String,PredicateConstructor>();


    /* fields for setting up the web server */
    private final HttpHandler defaultHandler = new DefaultHandler();
    private final HttpHandler fileServeHandler = new FileServeHandler(defaultHandler);
    private final HttpHandler apiHandler = new APIHandler(this);
    private final HttpHandler exitHandler = new ExitHandler();

    private HttpServer server;


    /* Constructors */
    
    public OMGServer() {
		this("data/copilot-generated.json", DEFAULT_PORT);
	}

    public OMGServer(String dataFile) {
        this(dataFile, DEFAULT_PORT);
    }

    public OMGServer(String dataFile, int port) {
        this.dataFile = dataFile;
        installDefaultHandlers();

        try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/static/", fileServeHandler);
            server.createContext("/favicon.ico", fileServeHandler);   // special
            server.createContext("/exit/", exitHandler);
            HttpContext apiContext = server.createContext("/api/", apiHandler);
            apiContext.getFilters().add(new ParameterFilter());
    
            server.createContext("/", defaultHandler);
            server.setExecutor(null); // creates a default executor
		} catch (java.net.BindException e) {
			System.out.println("BindException: " + e.getMessage());
			System.out.println("(Another copy of the server is probably already running.)");
			System.exit(-1);
		} catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

    }

    public void start() {
        server.start();
        System.out.println("Server started on port " + server.getAddress().getPort());
    }

    public OMGServer installHandler(String apiService, RequestHandler h) {
		handlers.put(apiService, h);
        return this;
	}	

    public <T> OMGServer installConstructor(String type, Class<T> klass, String... fields) {
		constrs.put(type, new DataConstructor<T>(klass, fields));
        return this;
	}

    public OMGServer installPredicate(String param, PredicateConstructor p) {
		predicates.put(param, p);
        return this;
	}

    public boolean hasHandlerFor(String apiService) {
        return handlers.containsKey(apiService);
    }

    public RequestHandler getHandlerFor(String apiService) {
        return handlers.get(apiService);
    }



    /* Methods for fetching data and returning as constructed (student) user objects */
   
    /**
     * Fetches a list of items from the data provider and returns them as a list of
     * user-specified object types.
     */
	public <T> List<T> fetchItemList() {
		try {
			DataProvider dp = new DataProvider(dataFile);
			return dp.fetchItemData(constrs);
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<T>();
		}
	}

    /**
     * Fetches a list of items from the data provider and returns them as a custom list
     * type, specified by "empty" and "cons" constructors.
     */
    public <S,T> T fetchItemList(ListEmptyConstructor<T> empty, ListConsConstructor<S,T> cons) {
		try {
			DataProvider dp = new DataProvider(dataFile);
			List data = dp.fetchItemData(constrs);
			T list = empty.create();
			for (Object d : data) {
				list = cons.create((S)d, list);
			}
			return list;
		} catch (IOException e) {
			e.printStackTrace();
			return empty.create();
		}
	}

    /**
     * Fetch and return a single, random item.
     */
	public <T> T fetchOneItemData() {
		List<T> data = fetchItemList();
        if (data.size() == 0) {
            throw new RuntimeException("No items in data.");
        }
		return data.get(new Random().nextInt(data.size()));
	}


    /* Methods for constructing predicates from API request query parameters */

    // these lists are used to determine the type of predicate that needs to be constructed for
    // each of the possible filter parameters in the API request
    private final static List<String> stringPredKeys = Arrays.asList(new String[] { "types", "search", "tags" });
	private final static List<String> intPredKeys = Arrays.asList(new String[] { "min-year", "max-year"});
	private final static List<String> pricePredKeys = Arrays.asList(new String[] { "min-price", "max-price" });

    /*
     * Takes a map of query parameter keys to values (also strings, really) and constructs a
     * predicate that can be used to filter items based on the conjunction of all those
     * filter parameters.
     */
	public Predicate constructPredicate(Map<String, Object> params) {
		return constructPredicate(params, false);
	}

    /*
     * typesOnly indicates whether to only consider the "types" parameter for 
     * constructing the predicate and ignore all other keys in the map.
     */
	public Predicate constructPredicate(Map<String, Object> params, boolean typesOnly) {
		Predicate p = defaultItemPredicate;

		for (String key : params.keySet()) {
			if (predicates.containsKey(key)) {
				if (!typesOnly || "types".equals(key)) {
					String value = (String)params.get(key);
					if (stringPredKeys.contains(key)) {
						if (value == null) { value = ""; }
						p = p.and( predicates.get(key).create(value) );
					} else if (intPredKeys.contains(key)) {
						if (value == null) { value = "0"; }
						p = p.and( predicates.get(key).create(Integer.parseInt(value)) );					
					} else if (pricePredKeys.contains(key)) {
						if (value == null) { value = "0"; }
						p = p.and( predicates.get(key).create(Utils.extractPriceAsCents(value)));					
					}
				}
			}
		}

		return p; 
	}


    /*
     * 
     */
    private void installDefaultHandlers() {
		handlers.put("item-data", new RequestItemDataHandler() {
			public String getResponse(int id) {
				return "false";
			}});
		handlers.put("catalog", new RequestCatalogHandler() {
			public String getResponse() {
				return "[]";
			}});
		handlers.put("count", new RequestCountHandler() { public String getResponse() { return "0"; }});
		handlers.put("price-range", new RequestRangeHandler() {
			public String getResponse() { return "{\"min\" : 0, \"max\" : 100000}"; }});
		handlers.put("year-range", new RequestRangeHandler() {
			public String getResponse() { return "{\"min\" : 0, \"max\" : 2050}"; }});
		handlers.put("tags", new RequestTagsHandler() {
			public String getResponse() {
				return "[]";
			}});

		handlers.put("cart-count", new RequestCountHandler() {
			public String getResponse() { return "0"; }});
		handlers.put("cart-list", new RequestCatalogHandler() {
			public String getResponse() { return "[]"; }});
        handlers.put("cart-subtotal", new RequestHandler() {
            public String getResponse() { return "0"; }});
        handlers.put("cart-total", new RequestHandler() {
            public String getResponse() { return "0"; }});
        handlers.put("cart-add", new RequestItemDataHandler() {
            public String getResponse(int id) { return "false"; }});
        handlers.put("cart-remove", new RequestItemDataHandler() {
            public String getResponse(int id) { return "false"; }});
        handlers.put("cart-get-coupon", new RequestHandler() {
            public String getResponse() { return "\"\""; }});
        handlers.put("cart-apply-coupon", new RequestCouponHandler() {
            public String getResponse(String code) { return "false"; }});
        handlers.put("cart-remove-coupon", new RequestCouponHandler() {
            public String getResponse(String code) { return "false"; }});    
	}
}
