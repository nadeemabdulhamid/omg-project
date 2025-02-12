# OMG Store Project - Development Plan

These do not necessarily correspond to individual lectures. More often they will encompass topics from more than one lecture/class session.


### Stage 1 - Simple classes

- `Book`
    - boilerplate vs enhanced constructor
- Simple methods
    - writtenBy
    - salePrice         (if   price > 0)  <--- make sure **
    - toJSONString


### Stage 2 - Containment

- `Author`, `Price`, `Store` (with fixed books)
    - overloaded constructors
- Methods

- Extension:
    - add description/full/short
    - add `Rating` class (see stage05.ext in omg-v1)
         average and count


### Stage 3 - Unions

- `IMedia`, `Book`, `Movie`, `Song`
    - (make sure contains() uses toLowerCase)
- introduce static methods - formatAsDollarsString and quoteString, to avoid repetition

Extension:
    IPrice, SimplePrice, DiscountPrice
    - for buildPrice, ensure  price > 0 && ...


### Stage 4 - Lists


Extension:
    tags list
    with limit



### Stage 5 (was 8) - Mutation

- Cart, v1 ---   ILoN      
    - use asJSONList in ILo
    -list, add, remove, count

- Aside: model authors, books, circular referrence

Extension
     - apply, remove, get coupon (single one, "50%OFF")



### Stage 6 (was 5) - Abstraction, Class Hierarchies, and Exceptions

- lift fields to abstract class,
  and then methods of the IMedia hierarchy
    - for buildPrice, ensure  price > 0 && ...



- introduce JSON objects instead of manually constructing strings   *****
 - IPrice change: Object toJSON();    -- talk about Object as superclass of everything (String and JSONObject)

- do year range (see extension for price range)
        (accumulator style methods)
    - Exceptions - RangeException,   superclass constructor, hierarchy checked/unchecked
- add year to info line

- change infoAsJSON() to findById()  ; produce an exception if not found
- introduce containsId()  as guard



Extension:
    - isEmpty() in list
    - price range  (Range class) 
    - add star-icons to rating with JSONArray   --- see stage05.ext.Rating in omg-v1


### Stage 7 (was 6) - Abstracting Types (Generics)

- ILo<T>    --- replace ILoS,   but discuss problems with ILoM ... see extension
- make cart... ILo<Integer>    <---- talk about wrapper types

Extension:
    - add 
	boolean isEmpty();
  	int count(T obj);
	ILo<T> removeAll(T obj);
	T getFirst();
     to ILo<T>

    implement tag tallying --- ListHelpers static method



### Stage 8 (was 7) - Abstracting Behavior (Function Objects)
- goal: handle Predicate parameter versions of API methods

- Abstract over priceRange and yearRange in ILoM
- Add to ILoM:
    - provide onlyPrint() after2000() --- abstract over them
    - now define a PrintPredicate and a VideoPredicate and AudioPredicate --- abstract over them to get a TypesPredicate
    - then install TypesPredicate... and define overloaded Store::catalog(Predicate p),  also tagsCount(Predicate p, Search tagSearch) <-- implemeent filter for ILo

- organize classes in subpackages (package/public)

Extension
    - textsearch predicate  (make sure contains() uses toLowerCase)
    - do min/max-price/year
    - implement RequestRangeWithFilterHandler  for price-range, year-range



### Stage 9 - Built-in Lists

- Replace with List<...>
    - item tags: IMedia, AbsMedia, StringHelpers (use `new JSONArray(this.tags)` in AbsMedia), rewrite collectTags() with for-each loop
    - change cart to List<Integer>      <---   note JSONArray can be initialized from a List<...>;   list.remove(int vs Integer) <--- ambiguous

    - break itemInfoAsJSON - into findItem(id) and then .toJSONString() *******  <--- try/catch
    - change Store::items to List<IMedia> --- rewrite to use  for-each loops and JSON objects
        - catalog(Predicate<IMedia> p), itemInfoAsJSON
        - get rid of ListHelpers
    - Store::tagCounts with predicate, helper functions

- loops

Extension:
    - turn ILoM::range() into Store::range(List<Integer> )  and handle predicate too in the rangehandler
        - revisit Range and define as mutable
    - support "tags" predicate -- add hasAnyTag() to IMedia
    - add "count" API handler
    - add star-icons to rating with JSONArray   (loop) --- see stage05.ext.Rating in omg-v1



### Stage 10 - Maps

- add ICoupon interface and a map of coupons (List<Item> -> int) , make some implementations, check validity of applyCoupon
- implement cart-total and cart-subtotal
    - add a Map of coupon codes to coupon objects  (use  Map.of(....) to construct)





### Stage 11 - Sorting
    - itemsMatching()  helper function
    - overload a catalog() method with  year sorting, using a YearComparator
    - introduce a Map of sorts to the Store
    - incorporate the lookup sort in catalog()
    - do title as well

Extension:
    price, rating


### Stage 12 - Streams

    - inner classes, anonymous classes, functional interfaces, lambda functions
    - rewrite existing methods
    - implement sorted tag counts with limit

    https://stackoverflow.com/questions/44180101/in-java-what-are-the-advantages-of-streams-over-loops

Extension (not related to streams):
    finalize data format for the input .json file -- add images, sale/list/discount in the data, remove buildPrice,
            add format, minutes, duration, etc.
            - image, descriptoin
            -  pages, publisher, duration
            
    change AbsMedia constructor to take   List<String>   for tags, instead of splitting up on commas


Exception testing:
		assertThrows(RuntimeException.class, () -> this.tardos.addBook(taocp2));
