/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

/**
 * Response to "/api/...-range?..." requests, where the query parameters
 * are ignored.
 * 
 * Should return a JSON object with two fields: { "min": <min>, "max": <max> }
 * indicating the minimum and maximum values of the particular type of range.
 */
@FunctionalInterface
@SuppressWarnings("rawtypes")
public interface RequestRangeHandler extends RequestHandler {
	// retain the basic   getRequest()   signature

	@Override default String getResponse(Predicate f) { return getResponse(); }
}
