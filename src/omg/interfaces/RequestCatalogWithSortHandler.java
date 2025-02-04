/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

/**
 * Response to the "/api/catalog?..." request, where the query parameters
 * are used by the server to build the filter predicate for the catalog list,
 * and the "?sort=...&order=[asc|desc]" query parameters are used to specify
 * a sort field and order.
 * 
 * Should return a JSON list of item ids in the store catalog that meet
 * the filter, in the given sort order.
 */
@FunctionalInterface
@SuppressWarnings({ "rawtypes" })
public interface RequestCatalogWithSortHandler extends RequestHandler {
	@Override default String getResponse() { throw new RuntimeException("unimplemented"); }

	String getResponse(Predicate f, String sortfield, boolean ascending);
}
