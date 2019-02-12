package logic.item.level;

import logic.item.Item;

public interface Level {
	public void improve(Item item);
	
	public boolean isNotMax();
	
	public boolean isUpperOrEquals(Item item);
	
	public int compareTo(Item item);
	
	public int getIndex();
}
