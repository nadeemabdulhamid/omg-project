/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

/**
 * Response to the "/api/catalog?..." request, where the query parameters
 * are used by the server to build the filter predicate for the catalog list.
 * 
 * Should return a JSON list of item ids in the store catalog that meet
 * the filter.
 */
@FunctionalInterface
@SuppressWarnings({ "rawtypes" })
interface RequestCatalogWithFilterHandler extends RequestHandler {
	@Override default String getResponse() { throw new RuntimeException("unimplemented"); }

	String getResponse(Predicate f);

	// ignore the sort and just filter...
	@Override
	default String getResponse(Predicate f, String sortfield, boolean ascending) {
		return getResponse(f);
	}
}
