/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

/**
 * Response to "/api/...-range?..." requests, where the query parameters
 * are used by the server to build the filter predicate for the count.
 * 
 * Should return a JSON object with two fields: { "min": <min>, "max": <max> }
 * indicating the minimum and maximum values of the particular type of range
 * for the filtered items.
 */
@FunctionalInterface
@SuppressWarnings({ "rawtypes" })
public interface RequestRangeWithFilterHandler extends RequestHandler {
	@Override default String getResponse() { throw new RuntimeException("unimplemented"); }

	String getResponse(Predicate f);
}
