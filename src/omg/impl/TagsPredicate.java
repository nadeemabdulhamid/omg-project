package omg.impl;

import java.util.function.Predicate;

class TagsPredicate implements Predicate<IItem> {
	String[] tags;

	public TagsPredicate(String tags) {
		super();
		this.tags = tags.split(",");
	}
	
	@Override
	public boolean test(IItem b) {
		return b.hasAnyTag(tags);
	}
}