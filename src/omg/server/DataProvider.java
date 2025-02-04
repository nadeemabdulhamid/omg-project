/* 
    OMG Project
    Copyright (c) 2025 Nadeem Abdul Hamid
    License: MIT
 */
package omg.server;

import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;
import org.json.*;

/**
 * Loads JSON item data and provides functionality to construct objects from it
 * by unifying the data schema with a target class constructor.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DataProvider {
	// Primitive classes for Java types, used for the schema
	private static Class<?> booleanKlass = Class.forPrimitiveName("boolean");
	private static Class<?> intKlass = Class.forPrimitiveName("int");
	private static Class<?> doubleKlass = Class.forPrimitiveName("double");

	// The raw JSON data from file
	private JSONArray database;

	// A simple schema of the JSON data
	private Map<String, Class<?>> dataSchema = new HashMap<>();
	
	
	/**
	 * Loads and prepares the JSON data from the given path.
	 * @param path The path to the JSON file
	 * @throws IOException
	 */
	public DataProvider(String path) throws IOException {
		loadDatabase(path);
		buildSchema();
	}

	/**
	 * Constructs a list of items from the JSON data, using constructors provided for
	 * the possible types of items ("audio", "print", or "video").
	 * 
	 * If a particular type of item does not have a constructor in the map, it is ignored.
	 * 
	 * @param <T> the user (student's) interface type for the union of possible items
	 * @param constrs A map of item types to constructors
	 * @return A list of constructed items
	 */
	public <T> List<T> fetchItemData(Map<String, DataConstructor> constrs) {

		// construct a map of constructors for each type that can successfully unify against the schema
		Map<String, Constructor> constrsMap = new HashMap<>();
		for (String type : constrs.keySet()) {
			Constructor c = findConstructorFor(type, constrs);
			if (c != null) {
				constrsMap.put(type, c);
			}
		}

		ArrayList<T> ts = new ArrayList<T>();	// the items to return

		for (Object obj : database) {
			JSONObject j = (JSONObject) obj;
			String type = j.getString("type");
			if (constrsMap.containsKey(type)) {		// found an installed constructor for this type
				DataConstructor cp = constrs.get(type);  // need the field names to extract
				Constructor cn = constrsMap.get(type);   // get the constructor itself
				Object[] args =  Stream.of(cp.fields).map(f -> { 
					Object v = j.get(f);
					//System.out.println(v + " : " + v.getClass());
					if (v.getClass().equals(JSONArray.class)) {
						return ((JSONArray)v).toList();	
					} else if (v.getClass().equals(BigDecimal.class) && dataSchema.get(f).equals(doubleKlass)) {
						return ((BigDecimal)v).doubleValue();
					} else {
						return j.get(f);
					}
				})
						.toArray();
				try {
					ts.add((T)cn.newInstance(args));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		return ts;
	}


	/*
	 * Loads the JSON data from the given path.
	 * 
	 * @param path The path to the JSON file
	 * @throws IOException If the file cannot be read
	 */
	private void loadDatabase(String path) throws IOException {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(path);
		JSONTokener jt = new JSONTokener(in);
		database = (JSONArray)jt.nextValue();
		//System.out.println("Loaded: " + database);
	}
	

	/*
	 * Maps JSON value types to Java types.
	 * @param value The JSON value to determine an appropriate Java type for
	 */
	private Class<?> javaTypeForJSONValue(Object value) {
		if (value instanceof Integer) { 
			return intKlass;
		} else if (value instanceof Double || value instanceof java.math.BigDecimal) {
			return doubleKlass;
		} else if (value instanceof String) {
			return String.class;
		} else if (value instanceof Boolean) {
			return booleanKlass;
		} else if (value instanceof JSONArray && !((JSONArray)value).isEmpty()) {
			return List.class;
		} else {
			System.out.println("Unsupported type: " + value.getClass().getName() + " (" + value + ")");
			return null;
		}
	}
	

	/*
	 * Builds a simple schema of the JSON data.
	 */
	private void buildSchema() {
		if (database.length() == 0) {
			throw new RuntimeException("Database is empty");
		}
		for (Object o : database) {
			JSONObject obj = (JSONObject)o;
			for (String key : obj.keySet()) {
				if (!dataSchema.containsKey(key)) {
					Object value = obj.get(key);
					Class klass = javaTypeForJSONValue(value);
					if (klass != null) dataSchema.put(key, klass);
				}
			}
		}
	}


	/*
	 * Finds a constructor for a given type of item ("print", "audio", or "video") that can be 
	 * unified against the JSON data schema.
	 * 
	 * @param type The type to find a constructor for
	 * @param constrs The map of types to constructor patterns
	 * @return A matching constructor if found, or null
	 */
	private Constructor findConstructorFor(String type, Map<String, DataConstructor> constrs) {
		if (!constrs.containsKey(type)) {
			return null;
		}

		DataConstructor cp = constrs.get(type);
		Class[] paramTypes = Stream.of(cp.fields).map(f ->  dataSchema.get(f)).toArray(Class[]::new);

		try {
			Constructor k = cp.klass.getConstructor(paramTypes);
			System.out.println("Found constructor: " + k);
			return k;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		return null;
	}

}