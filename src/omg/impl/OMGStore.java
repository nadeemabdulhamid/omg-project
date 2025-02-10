/* 
OMG Project
Copyright (c) 2025 Nadeem Abdul Hamid
License: MIT
*/
package omg.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.json.JSONObject;

import omg.server.OMGServer;
import omg.interfaces.*;

/**
* Reference implementation. Somewhat intentionally written in an obfuscated style.
*/
public class OMGStore {
	
	private List<IItem> items;
	private Map<String, Comparator<IItem>> comps = new HashMap<String, Comparator<IItem>>();
	private Map<String, Coupon> coupons = new HashMap<String, Coupon>();
	
	List<Integer> cart = new ArrayList<Integer>();
	String couponCode = "";
	
	public OMGStore(OMGServer server) {
		setupServer(server);
		setupMaps();
		items = server.fetchItemList();
		
		// alternate -- loading data as custom defined functional linked list
		ILoI loitems = server.fetchItemList(MTLoI::new, ConsLoI::new);
		System.out.println(loitems.count());
	}
	
	@SuppressWarnings({ "unchecked" })
	private void setupServer(OMGServer server) {
		server.installConstructor("audio", JSONItem.class);
		server.installConstructor("print", JSONItem.class);
		server.installConstructor("video", JSONItem.class);
		
		// filtering
		server.installPredicate("types", 	  (StringPredicateConstructor) TypePredicate::new);
		server.installPredicate("search", 	  (StringPredicateConstructor) SearchPredicate::new);
		server.installPredicate("tags", 	  (StringPredicateConstructor) TagsPredicate::new);
		server.installPredicate("min-price",  (IntPredicateConstructor) MinPricePredicate::new);
		server.installPredicate("max-price",  (IntPredicateConstructor) MaxPricePredicate::new);
		server.installPredicate("min-year",   (IntPredicateConstructor) MinYearPredicate::new);
		server.installPredicate("max-year",   (IntPredicateConstructor) MaxYearPredicate::new);
				
		// API HANDLERS
		
		// product page
		server.installHandler("catalog",   (RequestCatalogWithSortHandler) this::itemCatalogWithSort);
		server.installHandler("count",     (RequestCountWithFilterHandler) (f) -> Long.toString(items.stream().filter(f).count()));
		server.installHandler("item-data", (RequestItemDataHandler) (id) -> getItemById(id).asJSON().toString());
		server.installHandler("tags", 	   (RequestTagsWithFilterAndLimitHandler) this::tagArrayWithLimit);
		server.installHandler("price-range", (RequestRangeWithFilterHandler) this::priceRange);
		server.installHandler("year-range", (RequestRangeWithFilterHandler) this::yearRange);
		
		// cart service
		server.installHandler("cart-count",         (RequestCountHandler) () -> "" + cart.size());
		server.installHandler("cart-add",           (RequestItemDataHandler) this::cartAdd);
		server.installHandler("cart-remove",        (RequestItemDataHandler) this::cartRemove);
		server.installHandler("cart-list",          (RequestCatalogHandler) (() -> "[ " + 
								cart.stream().map((i) -> "" + i).collect(Collectors.joining(",")) + " ]"));
		server.installHandler("cart-subtotal",      (RequestHandler) this::cartSubtotal);
		server.installHandler("cart-total",         (RequestHandler) this::cartTotal);
		server.installHandler("cart-get-coupon",    (RequestHandler) this::getCoupon);
		server.installHandler("cart-apply-coupon",  (RequestCouponHandler) this::applyCoupon);
		server.installHandler("cart-remove-coupon", (RequestCouponHandler) this::removeCoupon);
	}

	private void setupMaps() {
		// sorting
		comps.put("", (b1, b2) -> 0);
		comps.put("title", (b1, b2) -> b1.getTitle().compareTo(b2.getTitle()));
		comps.put("price", (b1, b2) -> ((Integer)b1.salePrice()).compareTo(b2.salePrice()));
		comps.put("year", (b1, b2) -> ((Integer)b1.getYear()).compareTo(b2.getYear()));
		comps.put("rating", (b1, b2) -> ((Double)b1.getRating()).compareTo(b2.getRating()));
		
		// coupons
		coupons.put("", new NoDiscountCoupon());
		coupons.put("50%OFF", (items) -> items.stream().collect(Collectors.summingInt(IItem::salePrice)) / 2);
		coupons.put("AUDIO50", (items) -> {
			Predicate<IItem> isaudio = (i) -> i.isType("audio");
			return items.stream().filter(isaudio.negate()).collect(Collectors.summingInt(IItem::salePrice))
			+ items.stream().filter(isaudio).collect(Collectors.summingInt(IItem::salePrice)) / 2;
		});
	}
	
	public String itemCatalogWithSort(Predicate<IItem> f, String sortfield, boolean ascending) {
		Comparator<IItem> sortComp = comps.get(sortfield);
		return "[ " +
		items.stream()
		.filter(f)
		.sorted(ascending ? sortComp : sortComp.reversed())
		.map((b) -> b.getId())
		.map(Objects::toString)
		.collect(Collectors.joining(", "))
		+ "]";
	}
	
	public IItem getItemById(int id) {
		return items.stream().filter((b) -> b.getId() == id).findFirst().get();
	}
	
	private static class TagTally implements Comparable<TagTally> {
		String tag;
		long count;
		
		public TagTally(String tag, long count) {
			this.tag = tag;
			this.count = count;
		}
		
		public int compareTo(OMGStore.TagTally that) {
			long diff = this.count - that.count;
			return (diff < 0) ? -1 : (diff > 0 ? +1 : 0);
		}
	}
	
	private String tagArrayWithLimit(Predicate<IItem> itemFilter, String tagFilterText, int limit) {
		List<String> alltags = items.stream().filter(itemFilter)
		.flatMap((b) -> b.getTags().stream())
		.filter((t) -> tagFilterText.equals("") ? true : t.contains(tagFilterText))
		.toList();
		return "[ " +
		alltags.stream().distinct()
		.map((t) -> new TagTally(t, alltags.stream().filter((s) -> t.equals(s)).count()))
		.sorted(Comparator.reverseOrder())
		.limit(limit >= 0 ? limit : alltags.size())
		.map((tc) -> String.format("[\"%s\", %d]", tc.tag, tc.count))
		.collect(Collectors.joining(", "))
		+ " ]";
	}   
	
	// price range with filtering (should be only on types)
	public String priceRange(Predicate<IItem> f) {
		List<Integer> prices = items.stream().filter(f).map(IItem::salePrice).toList();
		return minMaxRange(prices);
	}
	
	// year range with filtering (should be only on types)
	public String yearRange(Predicate<IItem> f) {
		List<Integer> years = items.stream().filter(f).map(IItem::getYear).toList();
		return minMaxRange(years);
	}

	private String minMaxRange(List<Integer> vals) {
		Optional<Integer> min = vals.stream().collect(Collectors.minBy(Integer::compare));
		Optional<Integer> max = vals.stream().collect(Collectors.maxBy(Integer::compare));
		if (min.isEmpty() || max.isEmpty()) {
			return "false";
		} else {
			JSONObject obj = new JSONObject();
			obj.put("min", min.get());
			obj.put("max", max.get());
			return obj.toString();
		}
	}
	
	// note: intentionally obfuscated
	public String cartAdd(int id) {
		return !cart.contains(id) ? ((Supplier<String>)(() -> { cart.add((Integer)id); return "true"; })).get() 
		: "false";
	}
	
	public String cartRemove(int id) {
		return cart.contains(id) ? ((Supplier<String>)(() -> { cart.remove((Integer)id); return "true"; })).get() 
		: "false";
	}
	
	public String cartTotal() {
		int sum = coupons.get(this.couponCode).calculateSum(cart.stream().map(this::getItemById).toList());
		return "\"" + "$" + new BigDecimal(sum).divide(new BigDecimal(100)).setScale(2).toString() + "\"";
	}
	
	public String cartSubtotal() {
		int sum = coupons.get("").calculateSum(cart.stream().map(this::getItemById).toList());
		return "\"" + "$" + new BigDecimal(sum).divide(new BigDecimal(100)).setScale(2).toString() + "\"";
	}
	
	// "" means none applied
	public String getCoupon() {
		return "\"" + this.couponCode + "\"";
	}
	
	public String removeCoupon(String code) {
		if (couponCode.equals(code)) {
			couponCode = "";
			return "true";
		} else {
			return "false";
		}
	}
	
	public String applyCoupon(String code) {
		if (couponCode.equals("") ||
			!coupons.containsKey(code)) {
			return "false";
		} else {
			couponCode = code;
			return "true";
		}
	}
	
}