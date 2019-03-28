package ac.umons.slay.g01.logic.item.level;

import ac.umons.slay.g01.logic.item.Item;

public interface Level {
	public String type = Level.class.getName();
	public boolean isNotMax();
	
	public boolean isUpperOrEquals(Item item);
	
	public int compareTo(Item item);
	
	public int getIndex();
}
