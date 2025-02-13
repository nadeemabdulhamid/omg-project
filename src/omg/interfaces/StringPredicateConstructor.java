/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

public interface StringPredicateConstructor<T> extends PredicateConstructor<T> {
	Predicate<T> create(String s);
	default Predicate<T> create(int i) { return create(Integer.toString(i)); }
} 
