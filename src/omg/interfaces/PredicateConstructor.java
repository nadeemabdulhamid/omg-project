/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

import java.util.function.Predicate;

/**
 * A functional interface for creating user-defined Predicate objects which
 * have a single argument constructor (either a String or an int). Subclasses
 * of this interface will require one or the other constructor to be implemented.
 */
public interface PredicateConstructor<T> {
	default Predicate<T> create(String s) { throw new RuntimeException("unimplemented"); }
	default Predicate<T> create(int i) { throw new RuntimeException("unimplemented"); }
} 
