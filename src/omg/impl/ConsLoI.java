package omg.impl;

class ConsLoI implements ILoI {
	IItem first;
	ILoI rest;
	
	public ConsLoI(IItem first, ILoI rest) {
		this.first = first;
		this.rest = rest;
	}
	
	public String toString() { 
		return first + "," + rest.toString();
	}

	public int count() { 
		return 1 + rest.count();
	}
}