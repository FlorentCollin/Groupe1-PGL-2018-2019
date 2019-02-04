package logic.item;

public enum SoldierLevel {
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
}
