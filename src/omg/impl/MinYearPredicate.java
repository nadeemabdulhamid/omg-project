package omg.impl;

import java.util.function.Predicate;

class MinYearPredicate implements Predicate<IItem> {
	int minYear;

	MinYearPredicate(int minYear) {
		this.minYear = minYear;
	}
	
	@Override
	public boolean test(IItem b) {
		return b.getYear() >= minYear;
	}
}