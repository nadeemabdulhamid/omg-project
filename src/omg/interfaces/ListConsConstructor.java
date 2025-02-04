/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

/**
 * Represents the "cons" constructor for a user-defined, functional 
 * linked list class.
 */

@FunctionalInterface
public interface ListConsConstructor<S,T> {
	T create(S first, T rest);
}
