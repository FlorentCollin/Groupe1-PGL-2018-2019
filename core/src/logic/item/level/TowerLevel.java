package logic.item.level;

import logic.item.Item;

public enum TowerLevel implements Level{
	level1, level2, level3, level4;
	
	TowerLevel() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void improve(Item item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isNotMax() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUpperOrEquals(Item item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int compareTo(Item item) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIndex() {
		for(int i = 0; i<TowerLevel.values().length; i++) {
			if(TowerLevel.values()[i] == this) {
				return i;
			}
		}
		return -1;
	}

}
