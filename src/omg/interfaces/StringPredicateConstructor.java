/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

@SuppressWarnings({ "rawtypes" })
public interface StringPredicateConstructor extends PredicateConstructor {
	Predicate create(String s);
	default Predicate create(int i) { return create(Integer.toString(i)); }
} 
