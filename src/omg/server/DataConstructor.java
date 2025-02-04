/* 
    OMG Project
    Copyright (c) 2025 Nadeem Abdul Hamid
    License: MIT
 */
package omg.server;

/**
 * Represents the fields to be pulled from JSON data and unified with
 * the constructor of the target class.
 * 
 * If fields is empty, then expects the class to have a constructor that
 * takes a single JSONObject argument.
 */

public class DataConstructor<T> {
	public final Class<T> klass;
	public final String[] fields;

	public DataConstructor(Class<T> klass, String... fields) {
		this.klass = klass;
		this.fields = fields;
	}
}
