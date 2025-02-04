package omg.impl;

import java.util.function.Predicate;

class MaxPricePredicate implements Predicate<IItem> {
	int maxPrice;

	MaxPricePredicate(int maxPrice) {
		this.maxPrice = maxPrice;
	}
	
	@Override
	public boolean test(IItem b) {
		return b.salePrice() <= maxPrice;
	}
}