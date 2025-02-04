/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

/**
 * Responds to the "/api/tags?..." request, where the query parameters
 * are ignored.
 * 
 * Should return a list of tags and frequency counts for tags of all
 * items in the catalog.
 * "[ [tag1, count1], [tag2, count2], ... ]"
 */
@FunctionalInterface
@SuppressWarnings({ "rawtypes" })
public interface RequestTagsHandler extends RequestHandler {
    // retain the basic  getReponse() method signature

    // ignore search and limit...

    @Override 
	default String getResponse(Predicate f, String tagsearch, int limit) { return getResponse(); }

	@Override
	default String getResponse(Predicate f, String tagsearch) { return getResponse(); }
}
