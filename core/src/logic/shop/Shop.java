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
	}
	
	public void setSelectedItem(Item item) {
		selectedItem = item;
	}
	
	public Item getSelectedITem() {
		return this.selectedItem;
	}

}
