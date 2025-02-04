package omg.impl;

import java.util.List;
import java.util.stream.Collectors;

class NoDiscountCoupon implements Coupon {
	public int calculateSum(List<IItem> items) {
		return items.stream().collect(Collectors.summingInt(IItem::salePrice));
	}
}

