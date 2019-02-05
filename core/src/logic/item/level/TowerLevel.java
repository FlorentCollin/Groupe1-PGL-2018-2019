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

}
