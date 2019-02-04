package logic.item;

public class TestItem {
	public static void main(String[] args) {
		Soldier soldier = new Soldier(null);
		System.out.println("Buyable : "+soldier.getMode().isBuyable()); 
		System.out.println("Improvable : "+soldier.getMode().isImprovable());
		System.out.println("Movable : "+soldier.getMode().isMovable());
	
	}
}
