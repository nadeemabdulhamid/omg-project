# OMG Server - Back-end Setup

In order to provide the back-end functionality for the OMG server, several kinds of "plugins" (method) need to be provided via appropriate `install...` methods. Here is a general template covering all available facilities. Identifiers that are all uppercase indicate names that the user of the library needs to provide, based on their defined classes, and assuming that the handler methods are all defined in the same class as this setup method.

```
private void setupServer(OMGServer server) {
    // register constructors for each item type
    server.installConstructor("audio", AUDIO-ITEM-CLASS-NAME.class);
    server.installConstructor("print", PRINT-ITEM-CLASS-NAME.class);
    server.installConstructor("video", VIDEO-ITEM-CLASS-NAME.class);
    
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
    server.installHandler("catalog",   (RequestCatalogWithSortHandler) this::METHOD);
    server.installHandler("count",     (RequestCountWithFilterHandler) this::METHOD);
    server.installHandler("item-data", (RequestItemDataHandler) this::METHOD);
    server.installHandler("tags", 	   (RequestTagsWithLimitHandler) this::METHOD);
    server.installHandler("price-range", (RequestRangeWithFilterHandler) this::METHOD);
    server.installHandler("year-range", (RequestRangeWithFilterHandler) this::METHOD);
    
    // cart services
    server.installHandler("cart-count",         (RequestCountHandler) this::METHOD);
    server.installHandler("cart-add",           (RequestItemDataHandler) this::METHOD);
    server.installHandler("cart-remove",        (RequestItemDataHandler) this::METHOD);
    server.installHandler("cart-list",          (RequestCatalogHandler) this::METHOD);
    server.installHandler("cart-subtotal",      (RequestHandler) this::METHOD);
    server.installHandler("cart-total",         (RequestHandler) this::METHOD);
    server.installHandler("cart-get-coupon",    (RequestHandler) this::METHOD);
    server.installHandler("cart-apply-coupon",  (RequestCouponHandler) this::METHOD);
    server.installHandler("cart-remove-coupon", (RequestCouponHandler) this::METHOD);
}
```