/* 
    OMG Project
    Copyright (c) 2025 Nadeem Abdul Hamid
    License: MIT
 */
package omg.impl;

import java.math.BigDecimal;
import java.util.*;
import org.json.*;

/**
 * Reference implementation. Somewhat intentionally written in an obfuscated and
 * non-OO style.
 */
public class JSONItem implements IItem {
    JSONObject obj;

    public JSONItem(JSONObject obj) {
        this.obj = obj;
    }

    public String toString() {
        return "JSONItem: " + obj.toString();
    }

    @Override
    public int getId() {
        return obj.getInt("id");
    }

    @Override
    public boolean isType(String types) {
        return types.contains(obj.getString("type"));
    }

    @Override
    public JSONObject asJSON() {
    	JSONObject exp = new JSONObject();
    	
    	exp.put("id", getId());
    	exp.put("title", getTitle());
    	
        String image = obj.optString("image", null);
        String description = getFullDescription();
        List<String> tags = getTags();

    	if (image != null) {
    		exp.put("image", image);
    	}
    	if (description != null) {
	    	exp.put("description-short", getShortDescription());
	    	exp.put("description-full", getFullDescription());
    	}
    	
    	exp.put("tags", tags);

        String salePrice = obj.optString("sale-price", null);

    	if (salePrice != null) { exp.put("price", priceAsJSON()); }
    	
        double ratingAverage = getRating();
        int ratingCount = obj.optInt("rating-count", -1);
    	if (ratingAverage > 0) { 
    		exp.put("rating-average", ratingAverage); 
    		exp.put("star-icons", getStarsStyles());
    	}
    	if (ratingCount > 0) { exp.put("rating-count", ratingCount); }

        // item-specific
        if (isType("print")) {
            exp.put("type", "print");
            exp.put("author", obj.optString("author"));
            if (obj.optInt("pages") > 0 && obj.optInt("year") > 0  && obj.optString("publisher") != null) {
                exp.put("info-line", getInfoLine());
            }
        } else if (isType("audio")) {
            exp.put("type", "audio");
            exp.put("artist", obj.optString("artist"));
            if (obj.optInt("duration") > 0 && obj.optInt("year") > 0) {
                exp.put("info-line", getInfoLine());
            }
        } else if (isType("video")) {
            exp.put("type", "video");
            exp.put("starring", obj.optString("starring", null));
            exp.put("director", obj.optString("directed-by", null));
            if (obj.optInt("minutes", -1) > 0 && obj.optString("format", null) != null) {
                exp.put("info-line", getInfoLine());
            }
        } 
    	
    	return exp;
    }

    public Object priceAsJSON() {
        int salePrice = obj.optInt("sale-price", 0);
        int listPrice = obj.optInt("list-price", 0);
        String discount = obj.getString("discount");

        if (salePrice == listPrice) {
            return "$" + new BigDecimal(salePrice).divide(new BigDecimal(100)).setScale(2).toString();
        } else {
            JSONObject exp = new JSONObject();
            exp.put("sale", "$" + new BigDecimal(salePrice).divide(new BigDecimal(100)).setScale(2).toString());
            exp.put("list", "$" + new BigDecimal(listPrice).divide(new BigDecimal(100)).setScale(2).toString());
            exp.put("discount", discount);
            return exp;
        }
    }

    public String getShortDescription() {
        String description = getFullDescription();
		return description.substring(0, Math.min(description.length(), 20)) + "...";
	}
	
	public String getFullDescription() {
        String description = obj.optString("description-full", null);
		return description;
	}

    public JSONArray getStarsStyles() {
		JSONArray stars = new JSONArray();
        double ratingAverage = getRating();
    	for (int i = 0; i < 5; i++) {
    		if (ratingAverage >= i) {
    			stars.put("fa-solid fa-star");					// full
    		} else if (Math.floor(ratingAverage) == i) {
    			stars.put("fa-regular fa-star-half-stroke");   // partial
    		} else {
    			stars.put("fa-regular fa-star");				 // empty
    		}
    	}
    	return stars;
	}

    @Override
    public String getTitle() {
        return obj.getString("title");
    }

    @Override
    public int salePrice() {
        return obj.optInt("sale-price", 0);
    }

    @Override
    public int getYear() {
        return obj.optInt("year", 0);
    }

    @Override
    public double getRating() {
        return obj.optDouble("rating-average", -1);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<String> getTags() {
        return (List<String>) (List) obj.optJSONArray("tags").toList();
    }

    @Override
    public boolean hasAnyTag(String[] queryTags) {
        return Arrays.asList(queryTags).stream().anyMatch((qt) -> getTags().contains(qt));
    }

    @Override
    public boolean containsText(String query) {
        query = query.toLowerCase();
		return getTitle().toLowerCase().contains(query) 
				|| obj.optString("author", "").toLowerCase().contains(query) 
				|| obj.optString("artist", "").toLowerCase().contains(query) 
				|| obj.optString("starring", "").toLowerCase().contains(query) 
				|| obj.optString("directed-by", "").toLowerCase().contains(query) 
				|| getInfoLine().toLowerCase().contains(query)
				|| getFullDescription().toLowerCase().contains(query);
    }

    @Override
    public String getInfoLine() {
        if (isType("print")) {
            return obj.getInt("pages") + " • " + obj.getString("publisher") + " • " + obj.getInt("year");
        } else if (isType("audio")) {
            return (obj.getInt("duration")/60) + " minutes • " + obj.getInt("year");
        } else if (isType("video")) {
            return obj.getInt("minutes") + " minutes • " + obj.getString("format");
        } else {
            throw new UnsupportedOperationException("Unimplemented method 'getInfoLine'");
        }
    }
}
