/**
 * 
 */
package logic.shop;

import logic.board.District;
import logic.item.Item;

/**
 * @author Justin
 *
 */
public class Shop {
	private Item selectedItem;
	
	public void buy(District district) {
		district.setGold(district.getGold() - selectedItem.getMode().getPrice());
		selectedItem = null;
	}
	
	public void setSelectedItem(Item item, District district) {
		if(district.getGold() >= item.getMode().getPrice()) {
			selectedItem = item;
		}
	}
	
	public Item getSelectedItem() {
		return this.selectedItem;
	}

}
