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
	public void improve(Item item) {
		SoldierLevel newLevel = null;
		switch(((Soldier)item).getLevel()) {
		case level1:
			newLevel = level2;
			break;
		case level2:
			newLevel = level3;
			break;
		case level3:
			newLevel = level4;
			break;
		default:
			break;
		}
		((Soldier)item).setLevel(newLevel);
		
	}

	@Override
	public boolean isUpperOrEquals(Item item) {
		return this.price >= ((Soldier)item).getLevel().price;
	}
}
