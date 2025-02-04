/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

/**
 * Response to the "/api/catalog?..." request, where the query parameters
 * are ignored.
 * 
 * Should return a JSON list of item ids in the store catalog.
 */
@FunctionalInterface
@SuppressWarnings("rawtypes")
public interface RequestCatalogHandler extends RequestHandler {
    // retain the basic   getRequest()   signature

	// ignore filter and sort...
	
	@Override
	default String getResponse(Predicate f) { return getResponse(); }

	@Override
	default String getResponse(Predicate f, String sortfield, boolean ascending) {
		return getResponse();
	}
}
