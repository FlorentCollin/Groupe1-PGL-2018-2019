package item;

public enum SoldierLevel {
	level1(50), level2(100), level3(200), level4(400);
	private final int price;
	
	SoldierLevel(int price) {
		this.price = price;
	}
	
	public int getPrice() {
		return this.price;
	}
}
