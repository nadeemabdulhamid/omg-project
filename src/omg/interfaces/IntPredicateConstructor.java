/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

@SuppressWarnings({ "rawtypes" })
public interface IntPredicateConstructor extends PredicateConstructor {
	default Predicate create(String s) { return create(Integer.parseInt(s)); }
	Predicate create(int i);
} 
