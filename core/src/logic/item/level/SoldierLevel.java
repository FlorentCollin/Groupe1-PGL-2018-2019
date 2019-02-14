package logic.item.level;

import logic.item.Item;
import logic.item.Soldier;

public enum SoldierLevel implements Level{
	level1(10,2), level2(20,5), level3(40,14), level4(80,41);
	private final int price;
	private final int salary;
	
	SoldierLevel(int price, int salary) {
		this.price = price;
		this.salary = salary;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	public int getSalary() {
		return this.salary;
	}
	
	@Override
	public boolean isNotMax() {
		return this != level4;
	}

	@Override
	public boolean isUpperOrEquals(Item item) {
		return this.price >= ((Soldier)item).getLevel().price;
	}

	@Override
	public int compareTo(Item item) {
		return this.getIndex() - item.getLevel().getIndex();
	}

	@Override
	public int getIndex() {
		for(int i = 0; i<SoldierLevel.values().length; i++) {
			if(this == SoldierLevel.values()[i]) {
				return i;
			}
		}
		return -1;
	}
	
	
}
