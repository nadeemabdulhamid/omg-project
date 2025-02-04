package omg.impl;

import java.util.function.Predicate;

class MinPricePredicate implements Predicate<IItem> {
	int minPrice;

	MinPricePredicate(int minPrice) {
		this.minPrice = minPrice;
	}
	
	@Override
	public boolean test(IItem b) {
		return b.salePrice() >= minPrice;
	}
}