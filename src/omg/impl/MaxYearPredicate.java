package omg.impl;

import java.util.function.Predicate;

class MaxYearPredicate implements Predicate<IItem> {
	int maxYear;

	MaxYearPredicate(int maxYear) {
		this.maxYear = maxYear;
	}
	
	@Override
	public boolean test(IItem b) {
		return b.getYear() <= maxYear;
	}
}