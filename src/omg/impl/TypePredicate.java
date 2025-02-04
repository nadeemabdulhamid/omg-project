package omg.impl;

import java.util.function.Predicate;

class TypePredicate implements Predicate<IItem> {
	String targetTypes;
	
	TypePredicate(String targetTypes) {
		this.targetTypes = targetTypes;
	}
	
	@Override
	public boolean test(IItem t) {
		return t.isType(targetTypes);
	}
}