/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

/**
 * Responds to the "/api/tags?..." request, where the query parameters
 * are used by the server to build the filter predicate for the count,
 * and additionally the "...&tag-search=..." query parameter is passed
 * to the handler.
 * also the "...&tag-limit=<int>" query parameter is passed to the handler.
 * 
 * Should return a list of tags and frequency counts for all tags of
 * items in the catalog that match the filter criteria, but at most 
 * <limit> of them, when sorted in order of frequency.
 * "[ [tag1, count1], [tag2, count2], ... ]"
 */
@FunctionalInterface
@SuppressWarnings("rawtypes")
public interface RequestTagsWithLimitHandler extends RequestHandler {
	@Override default String getResponse() { throw new RuntimeException("unimplemented"); }

		// ignore filter, tagSearch
	@Override default String getResponse(Predicate f, String tagSearch, int limit) {
		return getResponse(limit);
	}

	// limit < 0  means igmore
	String getResponse(int limit);
}
