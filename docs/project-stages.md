# OMG Store Project - Development Plan

These do not necessarily correspond to individual lectures. More often they will encompass topics from more than one lecture/class session.


### Stage 1 - Simple classes

- `Book`
    - boilerplate vs enhanced constructor
- Simple methods
    - writtenBy
    - salePrice
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

- introduce static methods - formatAsDollarsString and quoteString, to avoid repetition

Extension:
    IPrice, SimplePrice, DiscountPrice


### Stage 4 - Lists

Extension:
    tags list
    with limit


### Stage 5 - Abstraction, Class Hierarchies, and Exceptions

- lift fields to abstract class,
  and then methods of the IMedia hierarchy

- introduce JSON objects instead of manually constructing strings   *****
 - IPrice change: Object toJSON();    -- talk about Object as superclass of everything (String and JSONObject)

- do year range (see extension for price range)
        (accumulator style methods)
    - Exceptions - RangeException,   superclass constructor, hierarchy checked/unchecked
- add year to info line

Extension:
    - isEmpty() in list
    - price range  (Range class) 
    - add star-icons to rating with JSONArray   --- see stage05.ext.Rating in omg-v1


### Stage 6 - Abstracting Types (Generics)

- ILo<T>

Extension:
    implement tag tallying

### Stage 7 - Abstracting Behavior (Function Objects)

- handle Predicate parameter versions of API methods


### Stage 8 - Mutation

- Cart, v1


### Stage 9 - Built-in Lists

- loops

Extension:
    - add star-icons to rating with JSONArray   (loop) --- see stage05.ext.Rating in omg-v1


### Stage 10 - Maps

- sorting, coupons

