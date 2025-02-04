/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

/**
 * Represents a (student) user-specified request handler for the API.
 * 
 * All methods should return JSON data in string format.
 */
@FunctionalInterface
@SuppressWarnings({ "rawtypes" })
public interface RequestHandler {
	String getResponse();
	default String getResponse(String code) { throw new RuntimeException("unimplemented"); }  // for coupons
	default String getResponse(Predicate f) { throw new RuntimeException("unimplemented"); }
	default String getResponse(Predicate f, String sortfield, boolean ascending) { throw new RuntimeException("unimplemented"); }
	default String getResponse(int id) { throw new RuntimeException("unimplemented"); }
	default String getResponse(Predicate f, String tagsearch) { throw new RuntimeException("unimplemented"); }
	default String getResponse(Predicate f, String tagsearch, int limit) { throw new RuntimeException("unimplemented"); }
}