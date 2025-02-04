package omg.impl;

import java.util.function.Predicate;

class SearchPredicate implements Predicate<IItem> {
	String query;

	public SearchPredicate(String query) {
		this.query = query;
	}

	@Override
	public boolean test(IItem b) {
		return b.containsText(query);
	}
}