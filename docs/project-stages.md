# OMG Store Project - Development Plan

These do not necessarily correspond to individual lectures. More often they will encompass topics from more than one lecture/class session.

> In class work == CodeCraft Studio Sessions ($C^2S^2$)

### Stage 0 - Setup and Welcome



### Stage 1 - Simple classes

- `Book`
    - boilerplate vs enhanced constructor
- Simple methods
    - writtenBy
    - salePrice         (if   price > 0)  <--- make sure **
    - updatePrice    (need equals() for tests,  toString to see )
    - toJSONString

        - templates
        - class diagrams

Extension:
    - Define Author (independent from Book)
    - Define Price
       (see versions at start of stage 2)


### Stage 2 - Containment

- `Author`, `Price`, `Store` (with fixed books)
    - overloaded constructors
- Methods

- Extension:
    - add description/full/short
    - add `Rating` class (see stage06.ext in omg-v1)
         average and count


### Stage 3 - Unions

- `IMedia`, `Book`, `Movie`, `Song`
    - (make sure contains() uses toLowerCase)
    - info-line in json
- introduce static methods - formatAsDollarsString and quoteString, to avoid repetition
        separate quote from formatAsDollarsString because later on when adding strings to JSONObject.put, don't want the extra ""s around the dollar string

Extension:
    IPrice, SimplePrice, DiscountPrice
    - for buildPrice, ensure  price > 0 && ...


### Stage 4 - Lists

    - Define ILoM
    - tests, examples
    - add methods
    - change Store to list of items
    - update Main  (fetch "tags" from data-4 --  will not be separated when run at this point)

Extension:

    prepare for cart representation
    - ILoN                  <----- work on in class/homework, with methods

    tags list with limit

    part 1
    - define ILoS                   <------ work on this in classwork
    - define `split` in StringHelpers, test
    part 2
    - replace `kind/genre` with `tags` in media to ILoS
    - fix constructors, add getTags() to IMedia
    ** - add stubs first, then implement
    - add asJSONList to ILoS  
        change IMedia::toJSONString to use  asJSONList on the tags
    - add join()
    part 3
    - add append to ILoS
    - add collectTags() to ILoM
    part 4
    - implement (RequestTagsWithLimitHandler) store::tagsList -- ignore limit at first
    - implement ILoS.take()
    - fix tagsList to handle limit


### Stage 5 (was 8) - Mutation

- Intro: model authors, books, circular referrence

- Cart, v1 ---   
    - add ILoN cart field to Store
    - list, add, remove, count
                                    exam question: difference between two versions of remove() - one all, one first

- change infoAsJSON() to findById()  ; produce null if not found 


Classwork
    - equals() method with cyclical data
    - chatgpt - exercise - forwarding packages between warehouses

Extension
     - apply, remove, get coupon (single one, "50%OFF")
            - add "cart-add-coupon", etc. handlers ****
    - Range                     <------ work on in class/homework
        (an OO, immutable version as alternate - on exam? )
        (exam question - how to avoid use of null, just based on Java techniques we've learned so far? (Define I.. with "Unknown...";   constructor order.)

### Stage 6 - Abstraction, Class Hierarchies, and Exceptions

Part 1
- lift fields to abstract class AbsMedia
- (add a `year` field in the process)
  and then lift methods of the IMedia hierarchy to AbsMedia
    - for buildPrice, ensure  price > 0 && ...

Part 2
- Demo JSONObject and JSONArray
- introduce JSON objects instead of manually constructing strings   *****
- add "info-line" to JSON

Part 3
 - Fix repercussions - In IPrice, ILoN, Author, etc.
 - IPrice change: Object toJSON();    -- talk about Object as superclass of everything (String and JSONObject)

- Change ILOM::findItem to throw exception.
Exception testing:
		assertThrows(RuntimeException.class, () -> this.tardos.addBook(taocp2));


Extensions:
- Change ILoN.asJSONList  to return JSONArray,  
        get rid of ILoN.join, get rid of ILoS.join

- Implement ILoM.yearRange(), priceRange()  handlers
    - add `yearRangeAsJSON`, `price...` to Store
        server.installHandler("year-range", (RequestRangeHandler) store::yearRangeAsJSON);
        server.installHandler("price-range", (RequestRangeHandler) store::priceRangeAsJSON);

- add star-icons to rating with JSONArray   --- see stage05.ext.Rating in omg-v1

Exam question: Fix Range.toJSONString to use JSONObject and then .toString



### Stage 7 - Abstracting Types (Generics)

- start with adding size() to ILoS

- ILo<T>    --- replace ILoS,ILoN       but discuss problems with ILoM ... see extension
    - size, asJSONList(), contains(), removeAll() in ILo.
- make cart... ILo<Integer>    <---- talk about wrapper types
    - use asJSONList in ILo

Extension:
    implement tag tallying --- ListHelpers static method

    - set up tests for stub in Store 

    - add to ILo<T>
  	int count(T obj);
	T getFirst();
	boolean isEmpty()  ----- default method in interface  (cannot access state, but avoid overhead of an extra abstract class)
     
    - implement private tagTally helper method
    - change to RequestTagsHandler in Main




### Stage 8 - Abstracting Behavior (Function Objects)
- goal: handle Predicate parameter versions of API methods

- Abstract over priceRange and yearRange in ILoM -- extractors with range() method
    - integrate with rangeAsJSON() in Store
    
- Add to ILoM:

    - provide onlyPrint() after2000() --- abstract over them ---- IMediaPredicate => After2000Predicate, PrintPredicate
        ILoM -> filter(IMediaPredicate)

    - now define a PrintPredicate and a VideoPredicate and AudioPredicate --- abstract over them to get a TypesPredicate
        - Note that TypesPredicate just overrides standard library Predicate<T>
        ILoM -> filter(Predicate<IMedia>)

    - then install TypesPredicate... and define overloaded Store::catalog(Predicate p),  also tagsCount(Predicate p, Search tagSearch) <-- implemeent filter for ILo

- organize classes in subpackages (package/public)

- abstract over "video", "print", etc. type constants in Audio, Book, Movie -- common field initialized through super call
    - lift typeMatches' to abstract class , and move the type from toJSONString into AbsItem.

Extension
    - textsearch predicate  (make sure contains() uses toLowerCase)
    - do min/max-price/year
    - implement RequestRangeWithFilterHandler  for price-range, year-range

    Optional: 
        - implement overloaded constructors for media that take JSONObject



#### Aside: Visitor Design Pattern



### Stage 9 - Built-in Lists

- Replace with List<...>
    - item tags: IMedia, AbsMedia, StringHelpers (use `new JSONArray(this.tags)` in AbsMedia), rewrite collectTags() with for-each loop

                /// (already done stage 5- break itemInfoAsJSON - into findItem(id) and then .toJSONString() *******  <--- try/catch)
    - change Store::items to List<IMedia> --- rewrite to use  for-each loops and JSON objects
        - catalog(Predicate<IMedia> p), itemInfoAsJSON
        - temporarily put bogus predicate in Store::tagCounts()

    - change cart to List<Integer>      <---   note JSONArray can be initialized from a List<...>;   list.remove(int vs Integer) <--- ambiguous

    - Store::tagCounts with predicate

- loops

- change AbsMedia constructor to take   List<String>   for tags, instead of splitting up on commas
        - ***** change data file for list of tags


Extension:
    - add "count" API handler
    - support "tags" predicate -- add hasAnyTag() to IMedia
    - rewrite star-icons in rating with loop 



### Stage 10 - Maps

- add ICoupon interface (List<Item> -> int) , make some implementations, check validity of applyCoupon
- in Store:  add a Map of coupon codes to coupon objects  (use  Map.of(....) to construct)
    - first explain using big if/else/statements
- implement cart-total and cart-subtotal

Extension:
    - add getType(), isOnSale() to IMedia -- delegate to IPrice --- (apply coupon only to things not on sale)
    - implement BOGOPair coupon
    - modify HalfOffCoupon so it only applies to items that are not already on sale
    - implement your own custom coupon



### Stage 11 - Sorting, Functional Interfaces
    - itemsMatching()  helper function
    - overload a catalog() method with basic id sorting, using Collections.sort() Collections.reverse()
        - IMedia extends Comparable
        - install RequestCatalogWithSortHandler
    - talk about Comparators
        - year sorting, using a YearComparator -- add to "if" statement in catalog(), test
        - inner classes, anonymous classes, functional interfaces, lambda functions
    - do title sorting as well

Extension:
    - introduce a Map of sorts to the Store
       - add price, rating sorting
    a coupon that involves sorting the items
        OldiesGoldiesCoupon


### Stage 12 - Streams

    - rewrite existing methods replace all for-each loops
    - implement sorted tag counts with limit

    https://stackoverflow.com/questions/44180101/in-java-what-are-the-advantages-of-streams-over-loops

Extension (not related to streams):
    (optionally with the JSONObject version of the constructors)

    support the full data format from the input .json file -- add
            - AbsMedia : image URL       "image" in JSON data/output
            - Book :  pages, publisher
            - Movie : minutes, format
                --- incorporate into the info-line()
    - can comment out testToJSONString



### Mechanics of VS Code

- Running main()
- Running tests, all tests in the project
- Debugging tests with breakpoints, adding watches
- Stopping a program
- See error # indicators on files in project explorer, or the "Problems" pane

### Building JavaDoc

```
javadoc -d ../javadoc --class-path ../lib/omg-project.jar:. main media funcobjs
```



### Extra Extensions

- Apply some discount across the list of items
- Allow multiple coupons
- Support separate user carts (cookies)




