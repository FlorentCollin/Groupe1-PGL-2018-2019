package logic.item;

public abstract class DestroyableItem extends Item{
	private int bonus;

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}

}
