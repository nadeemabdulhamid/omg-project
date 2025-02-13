/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

public interface IntPredicateConstructor<T> extends PredicateConstructor<T> {
	default Predicate<T> create(String s) { return create(Integer.parseInt(s)); }
	Predicate<T> create(int i);
} 
