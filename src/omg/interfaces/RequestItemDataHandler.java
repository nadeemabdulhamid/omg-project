/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

/**
 * Response to the "/api/item-data?id=<id>" request, where the id query parameter
 * is passed to the response method.
 * 
 * Should return a JSON object representing the item with the given id.
 */

@FunctionalInterface
public interface RequestItemDataHandler extends RequestHandler {
	@Override default String getResponse() { throw new RuntimeException("unimplemented"); }

	String getResponse(int id);
}
