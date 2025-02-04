/* 
    OMG Project
    Copyright (c) 2025 Nadeem Abdul Hamid
    License: MIT
 */
package omg.impl;

import java.util.List;

import org.json.JSONObject;

public interface IItem {
	int getId();
	boolean isType(String types);
	JSONObject asJSON();
	String getTitle();
	int salePrice();
	int getYear();
	double getRating();
	List<String> getTags();
	boolean hasAnyTag(String[] queryTags);
	boolean containsText(String query);
	String getInfoLine();
}
