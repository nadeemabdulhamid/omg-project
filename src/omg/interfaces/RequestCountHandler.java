/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

/**
 * Responds to the "/api/count?..." request, where the query parameters
 * are ignored.
 * 
 * Should return the number of items in the store catalog.
 */
@FunctionalInterface
@SuppressWarnings("rawtypes")
public interface RequestCountHandler extends RequestHandler {
	// retain the basic   getRequest()   signature

	@Override default String getResponse(Predicate f) { return getResponse(); }
}
