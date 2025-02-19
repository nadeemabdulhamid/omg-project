# OMG Server - API Endpoints

Handler types are defined as functional interfaces, so the name of the method doesn't really matter, as long as the signature provided is consistent with the interface type listed in the cast.

All raw price values are in whole numbers, representing cents. 


> ## Summary
> ### Product page endpoints
> - [`/api/catalog`](#catalog-endpoint) - JSON list of item ids
> - [`/api/item-data`](#item-data-endpoint) - JSON object with item fields
> - [`/api/count`](#count-endpoint) - JSON integer
> - [`/api/tags`](#tags-endpoint) - JSON list of strings or *[tag, frequency-count]* pairs
> - [`/api/price-range`](#priceyear-range-endpoints) and<br>
>   [`/api/year-range`](#priceyear-range-endpoints) - JSON object with `min` and `max` fields
> 
> ### Cart management endpoints
> - [`/api/cart-add`](#cart-addremove-endpoints) and <br>
>   [`/api/cart-remove`](#cart-addremove-endpoints) - JSON boolean
> - [`/api/cart-count`](#cart-count-endpoint) - JSON integer
> - [`/api/cart-list`](#cart-list-endpoint) - JSON list of item ids
> - [`/api/cart-subtotal`](#cart-subtotaltotal-endpoints) and<br>
>   [`/api/cart-total`](#cart-subtotaltotal-endpoints) - string formatted as dollar amount "$###.##"
> - [`/api/cart-apply-coupon`](#applyremove-coupon-endpoints) and<br>
>   [`/api/cart-remove-coupon`](#applyremove-coupon-endpoints) - JSON boolean
> - [`/api/cart-get-coupon`](#get-coupon-endpoint) - JSON string
> 
> ### Other
> - [Filter Parameters](#filter-parameters)


---

## Catalog Endpoint

- `/api/catalog?<filter-parameters>&sort=...&order=...`

### Query parameters

- See [Filter Parameters](#filter-parameters).
- `sort=<title|price|year|rating>` 
    - Indicates a field by which the list of item id's should be ordered.
- `order=<asc|desc>` 
    - Indicates whether the sort should be in increasing (`asc`ending) or decreasing order.

### Response format

A back-end handler method should return a string formatted as a JSON list, e.g.

    "[ id, id, id, ...]"

### Handlers

- Use `OMGServer.installHandler("catalog", (RequestCatalog...Handler)...)` to provide a back-end handler for this endpoint. There are three options for the handler interface:

    - [`RequestCatalogHandler`](../src/omg/interfaces/RequestCatalogHandler.java) - a method that takes no parameters and produces a `String`. This one is expected to simply provide the list of all items in the catalog, with no filtering or sorting.
        
        ```public String method()```

    - [`RequestCatalogWithFilterHandler`](../src/omg/interfaces/RequestCatalogWithFilterHandler.java) - a method that takes a `Predicate` on items and produces a `String`. This one is expected to provide a list of items in the catalog, filtered by the given criteria.

        ```public String method(Predicate f)```

    - [`RequestCatalogWithSortHandler`](../src/omg/interfaces/RequestCatalogWithSortHandler.java) - a method that takes a `Predicate` on items, a sort field name (possibly the empty string `""`), and a `boolean` (`true` if it should be in ascending order; `false` for descending), and produces a list of items in the catalog, filtered by the given predicate and sorted on the specified field (if non-empty), in the given order.

        ```String method(Predicate f, String sortfield, boolean ascending)```



## Item Data Endpoint

- `/api/item-data?id=<...>`

### Query parameters

- A single integer that is the `id` of the requested item.

### Response format

A back-end handler method should return a string formatted as a JSON object with the following fields (some are specific to the *type* of the media, as indicated.) All are optional unless noted as required.

```
{
    "type" : "<audio|print|video>", // required ***
    "id" : <num>,                   // integer - required ***
    "title" : "<text>",             // string - required ***
    "artist" : "<text>",            // string - for "audio"
    "author" : "<text>",            // string - for "print"
    "starring" : "<text>",          // string - for "video"
    "directed-by" : "<text>",       // string - for "video"
    "info-line" : "<text>",         // string - (depends on type)
    "image" : "<url>",              // string
    "description-short" : "<text>", // string
    "description-full" : "<text>",  // string
    "tags" : [ "t1", "t2", ... ],   // list of strings OR a single string
    "rating-average" : <num>,       // double/decimal number
    "star-icons" : ["...", ...],    // list of 5 icon style strings
    "rating-count" : <num>,         // integer
    "price" : <num|object>,         // integer, string, or nested object of the form:
                                    //   { "sale" : "$##.##", 
                                    //     "list" : "$##.##", 
                                    //     "discount" : "<text>" }
}
```

The `"star-icons"` strings should be either:
- `"fa-solid fa-star"` for a full star
- `"fa-regular fa-star-half-stroke"` for a partial star
- `"fa-regular fa-star"` for an empty star

### Handler

- Use `OMGServer.installHandler("item-data", (RequestItemDataHandler)...)` to provide a back-end handler for this endpoint. The handler should implement the [`RequestItemDataHandler`](../src/omg/interfaces/RequestItemDataHandler.java) interface with a method that takes a single integer parameter:

    - ```String method(int id)```



## Count Endpoint

- `/api/count?<filter-parameters>`

### Query parameters

- See [Filter Parameters](#filter-parameters).

### Response format

A back-end handler method should return an integer formatted as a string.

### Handlers

- Use `OMGServer.installHandler("count", (RequestCount...Handler)...)` to provide a back-end handler for this endpoint. There are two options for the handler interface:

    - [`RequestCountHandler`](../src/omg/interfaces/RequestCountHandler.java)  - a method that takes no parameters and produces an integer formatted as a `String`. This one is expected to simply provide the count of all items in the catalog, with no filtering.

        ```public String method()```

    - [`RequestCountWithFilterHandler`](../src/omg/interfaces/RequestCountWithFilterHandler.java) - a method that takes a `Predicate` on items and produces an integer formatted as a `String`. This one is expected to provide a count of items in the catalog that meet the given criteria.

        ```public String method(Predicate f)```



## Tags Endpoint

- `/api/tags?<filter-parameters>&tag-search=<...>&tag-limit=<...>`

### Query parameters

- See [Filter Parameters](#filter-parameters).
- `tag-search=<text>`
    - Indicates some text by which tags should be filtered (if they contain it as a substring)
- `tag-limit=<text>`
    - Indicates the maximum number of tags to be returned in the response list.

### Response format

A back-end handler method should return either:

- A JSON list of strings, 

      [ "programming", "software engineering", "coding", ... ]

- or, a JSON list of two-element lists, where the first element of each list should be a tag, and the second element is a frequency count. The list should be provided in decreasing order of frequency, for example:

      [ ["programming", 5], 
        ["software engineering", 5], 
        ["coding", 4], 
        ["technology", 3], 
        ... 
      ]

### Handlers

- Use `OMGServer.installHandler("tags", (RequestTags...Handler)...)` to provide a back-end handler for this endpoint. There are four options for the handler interface:

    - [`RequestTagsHandler`](../src/omg/interfaces/RequestTagsHandler.java) - a method that takes no parameters and produces a `String`. This one is expected to simply provide the list of all tags in the catalog, in the response format described above, preferably sorted by frequency (and then alphabetically).
        
        ```public String method()```

    - [`RequestTagsWithLimitHandler`](../src/omg/interfaces/RequestTagsWithLimitHandler.java) - a method that takes a single integer, `limit`. If the `limit` is negative, it can be ignored completely, and the handler produces the same as `RequestTagsHandler`. Otherwise (if `limit` is non-negative), this handler is expected to provide a list of only the first `limit` number of tags. 

        ```public String method(Predicate f, String tagSearch)```

    - [`RequestTagsWithFilterHandler`](../src/omg/interfaces/RequestTagsWithFilterHandler.java) - a method that takes a `Predicate` on items, and a tag search string, and produces a `String`. This one is expected to provide a list of tags, that contain the tag search string, for all items in the catalog filtered by the given criteria.

        ```public String method(Predicate f, String tagSearch)```

    - [`RequestTagsWithFilterAndLimitHandler`](../src/omg/interfaces/RequestTagsWithFilterAndLimitHandler.java) - a method that takes a `Predicate` on items, a tag search string, and an integer `limit`, and produces a `String`. This one is expected to provide a list of tags, that contain the tag search string, for all items in the catalog filtered by the given criteria. Only the first `limit` number of tags should be produced (unless `limit` is negative, in which case it is ignored), when sorted by frequency (and then alphabetically).

        ```String method(Predicate f, String tagSearch, int limit)```



## Price/Year Range Endpoints

- `/api/price-range?types=<audio|print|video>`
- `/api/year-range?types=<audio|print|video>`

### Query parameters

- Only supports the `types` [filter parameter](#filter-parameters).

### Response format

A back-end handler method should return a JSON object with `min` and `max` fields:

    { "min" : <value>, "max" : <value> }

### Handlers

- Use `OMGServer.installHandler("price-range", (RequestRange...Handler)...)`  <br>
 or `OMGServer.installHandler("year-range", (RequestRange...Handler)...)` <br>
 to provide a back-end handler for this endpoint. There are two options for the handler interface:

    - [`RequestRangeHandler`](../src/omg/interfaces/RequestRangeHandler.java) - a method that takes no parameters and produces a JSON object formatted as a `String`. This one is expected to simply provide the range of all prices/years of items in the catalog.

        ```public String method()```

    - [`RequestRangeWithFilterHandler`](../src/omg/interfaces/RequestRangeWithFilterHandler.java) - a method that takes a `Predicate` and produces a JSON object formatted as a `String`. This one is expected to provide the range of prices/years of the specified type(s) of items in the catalog.

        ```public String method(Predicate f)```



## Cart Add/Remove Endpoints

- `/api/cart-add?id=<...>`
- `/api/cart-remove?id=<...>`

### Query parameters

- A single integer that is the `id` of the requested item.

### Response format

A `true` or `false` string, indicating success of the operation.

### Handlers

- Use `OMGServer.installHandler("cart-add", (RequestItemDataHandler)...)`<br>
  or `OMGServer.installHandler("cart-remove", (RequestItemDataHandler)...)`<br>
  to provide back-end handlers for these endpoints. The handlers should implement the [`RequestItemDataHandler`](../src/omg/interfaces/RequestItemDataHandler.java) interface with a method that takes a single integer parameter:

    - ```String method(int id)```



## Cart Count Endpoint

- `/api/cart-count`

### Query parameters

- None.

### Response format

A back-end handler method should return an integer formatted as a string.

### Handler

- Use `OMGServer.installHandler("cart-count", (RequestCountHandler)...)` to provide a back-end handler for this endpoint. This one is expected to simply provide the count of items in the cart.

      public String method()




## Cart List Endpoint

- `/api/cart-list`

### Query parameters

- None.

### Response format

A back-end handler method should return a JSON list of integers formatted as a string, e.g.

    "[ id, id, id, ...]"

### Handler

- Use `OMGServer.installHandler("cart-list", (RequestCatalogHandler)...)` to provide a back-end handler for this endpoint. This one is expected to simply provide a list of ids of items in the cart.

      public String method()



## Cart Subtotal/Total Endpoints

- `/api/cart-subtotal` and
- `/api/cart-total`

### Query parameters

- None.

### Response format

A back-end handler method should return a string formatted as dollar amount, with two decimal places.

    "$###.##"

### Handlers

- Use `OMGServer.installHandler("cart-subtotal", (RequestHandler)...)` <br>
  or `OMGServer.installHandler("cart-total", (RequestHandler)...)` <br>
  to provide back-end handlers for these endpoints. This one is expected to simply provide a list of ids of items in the cart.

      public String method()



## Apply/Remove Coupon Endpoints

- `/api/cart-apply-coupon?code=<...>` and
- `/api/cart-remove-coupon?code=<...>`

### Query parameters

- A string that is the code of the intended coupon to apply or remove to/from the cart.

### Response format

A `true` or `false` string, indicating success of the operation.

### Handlers

- Use `OMGServer.installHandler("cart-apply-coupon", (RequestCouponHandler)...)`<br>
  or `OMGServer.installHandler("cart-remove-coupon", (RequestCouponHandler)...)`<br>
  to provide back-end handlers for these endpoints. The handlers should implement the [`RequestCouponHandler`](../src/omg/interfaces/RequestCouponHandler.java) interface with a method that takes a single `String` parameter:

    - ```String method(String code)```



## Get Coupon Endpoint

- `/api/cart-get-coupon`

### Query parameters

- None.

### Response format

A back-end handler method should return a string that is the currently applied coupon code, possibly the empty string:

    "<code>"

### Handler

- Use `OMGServer.installHandler("cart-get-coupon", (RequestHandler)...)` to provide a back-end handler for this endpoint. This one is expected to simply provide a JSON quoted string (possibly the empty string).

      public String method()





## Filter Parameters

Many of the endpoints documented above accept a set of query parameters that enables filtering for the requested information/data. To ensure that the server framework parses and constructs a `Predicate` for these parameters, the `OMGServer.installPredicate("<query-param-key>", ...)` method should be invoked, with an appropriate type of `PredicateConstructor` (either `(StringPredicateConstructor)` or `(IntPredicateConstructor)`), as documented below.

- `types=<audio|print|video>`
    - The type of media is ***one or more*** of these three (if more than one is specified, they are separated by commas). This field is required in both the input data file, and the JSON response from the server. To ensure it is handled, provide a `StringPredicateConstructor` to `installPredicate` with the constructor of a `Predicate` object that receives the type value(s).
    
        (Also, note, for each type in the data file,
    the Java backend should invoke `OMGServer.installConstructor("<type>", <class>, "<field-name>", ...)`.)

- `min-price=<...>` <br>
    `max-price=<...>` <br>
    `min-year=<...>` <br>
    `max-year=<...>` 
    - To handle any or all of these, provide an `IntPredicateConstructor` to `OMGServer.installPredicate` with the constructor of a `Predicate` object that receives the minimum/maximum integer value.

- `tags=<t1,t2,t3>`
    - A comma-separated list of tags to filter for. Provide a `StringPredicateConstructor` to `OMGServer.installPredicate` with the constructor of a `Predicate` object that receives the entire comma-separated string. (The predicate object can split it on the commas, as necessary.) An item should be selected if it has *any* of the tags listed.

- `search=<text>`
    - Indicates a text value to search for in any of the string fields of the item (e.g. title, author, publisher, format, etc.)
