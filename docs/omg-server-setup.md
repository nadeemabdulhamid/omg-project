# OMG Server - Back-end Setup

In order to provide the back-end functionality for the OMG server, several kinds of "plugins" (method) need to be provided via appropriate `install...` methods. Here is a general template covering all available facilities. Identifiers that are all uppercase indicate names that the user of the library needs to provide, based on their defined classes, and assuming that the handler methods are all defined in the same class as this setup method.

Examine the interface definitions in the type casts to see the signature of the method that needs to be provided. See below for a summary.

```
private void setupServer(OMGServer server) {
    // register constructors for each item type
    server.installConstructor("audio", AUDIO-ITEM-CLASS-NAME.class, "json-field", "json-field", ...);
    server.installConstructor("print", PRINT-ITEM-CLASS-NAME.class, "json-field", "json-field", ...);
    server.installConstructor("video", VIDEO-ITEM-CLASS-NAME.class, "json-field", "json-field", ...);
    
    // register constructors for various types of predicates to support filtering
    server.installPredicate("types", 	 (StringPredicateConstructor) TYPE-PREDICATE-CLASS::new);
    server.installPredicate("search", 	 (StringPredicateConstructor) SEARCH-PREDICATE-CLASS::new);
    server.installPredicate("tags", 	 (StringPredicateConstructor) TAGS-PREDICATE-CLASS::new);
    server.installPredicate("min-price", (IntPredicateConstructor) MINPRICE-PREDICATE-CLASS::new);
    server.installPredicate("max-price", (IntPredicateConstructor) MAXPRICE-PREDICATE-CLASS::new);
    server.installPredicate("min-year",  (IntPredicateConstructor) MINYEAR-PREDICATE-CLASS::new);
    server.installPredicate("max-year",  (IntPredicateConstructor) MAXYEAR-PREDICATE-CLASS::new);
          
    // register API handlers
    
    // product page services
    server.installHandler("catalog",   (RequestCatalogWithSortHandler) OBJ::METHOD);
            // or (RequestCatalogWithFilterHandler) or (RequestCatalogHandler) 
    server.installHandler("count",     (RequestCountWithFilterHandler) OBJ::METHOD);
            // or (RequestCountHandler)
    server.installHandler("item-data", (RequestItemDataHandler) OBJ::METHOD);
    server.installHandler("tags",      (RequestTagsWithFilterAndLimitHandler) OBJ::METHOD);
            // or (RequestTagsWithFilterHandler) or (RequestTagsWithLimitHandler) or (RequestTagsHandler)
    server.installHandler("price-range", (RequestRangeWithFilterHandler) OBJ::METHOD);
            // or (RequestRangeHandler)
    server.installHandler("year-range", (RequestRangeWithFilterHandler) OBJ::METHOD);
            // or (RequestRangeHandler)
    
    // cart services
    server.installHandler("cart-count",         (RequestCountHandler) OBJ::METHOD);
    server.installHandler("cart-add",           (RequestItemDataHandler) OBJ::METHOD);
    server.installHandler("cart-remove",        (RequestItemDataHandler) OBJ::METHOD);
    server.installHandler("cart-list",          (RequestCatalogHandler) OBJ::METHOD);
    server.installHandler("cart-subtotal",      (RequestHandler) OBJ::METHOD);
    server.installHandler("cart-total",         (RequestHandler) OBJ::METHOD);
    server.installHandler("cart-get-coupon",    (RequestHandler) OBJ::METHOD);
    server.installHandler("cart-apply-coupon",  (RequestCouponHandler) OBJ::METHOD);
    server.installHandler("cart-remove-coupon", (RequestCouponHandler) OBJ::METHOD);
}
```


## Summary of functional interface method signatures

| Interface | Method signature |
| --------- | ---------------- |
| `StringPredicateConstructor` | `CONSTRUCTOR(String str)` (should be the constructor of a class that implements `Predicate<T>`) |
| `IntPredicateConstructor` | `CONSTRUCTOR(int value)` (should be the constructor of a class that implements `Predicate<T>`) |
| | |
| `RequestCatalogHandler` | `String METHOD()` |
| `RequestCatalogWithFilterHandler` | `String METHOD(Predicate<T> p)` |
| `RequestCatalogWithSortHandler` | `String METHOD(Predicate<T> p, String sortField, boolean ascending)` |
| `RequestCountHandler` | `String METHOD()` |
| `RequestCountWithFilterHandler` | `String METHOD(Predicate<T> p)` |
| `RequestCouponHandler` |  `String METHOD(String code)` |
| `RequestHandler` | `String METHOD()` |
| `RequestItemDataHandler` | `String METHOD(int id)` |
| `RequestRangeHandler` | `String METHOD()` |
| `RequestRangeWithFilterHandler` | `String METHOD(Predicate<T> p)` |
| `RequestTagsHandler` | `String METHOD()` |
| `RequestTagsWithLimitHandler` | `String METHOD(int limit)` (limit &lt; 0 means ignore it) |
| `RequestTagsWithFilterHandler` | `String METHOD(Predicate<T> p, String tagSearch)` |
| `RequestTagsWithFilterAndLimitHandler` | `String METHOD(Predicate<T> p, String tagSearch, int limit)` |
| | |
