/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

/**
 * Response to the "/api/count?..." request, where the query parameters
 * are used by the server to build the filter predicate for the count.
 * 
 * Should return the number of items in the store catalog that match the filter.
 */
@FunctionalInterface
@SuppressWarnings({ "rawtypes" })
public interface RequestCountWithFilterHandler extends RequestHandler {
	@Override default String getResponse() { throw new RuntimeException("unimplemented"); }

	String getResponse(Predicate f);
}
