/**
 * 
 */
package ac.umons.slay.g01.logic.shop;

import ac.umons.slay.g01.logic.board.District;
import ac.umons.slay.g01.logic.item.Item;

/**
 * @author Justin
 *
 */
public class Shop {
	private Item selectedItem;
	
	public void buy(District district) {
		district.setGold(district.getGold() - selectedItem.getPrice());
		selectedItem = null;
	}
	
	public void setSelectedItem(Item item, District district) {
		if(district.getGold() >= item.getPrice()) {
			selectedItem = item;
		}
	}
	public void setSelectedItem(Item item) {
	    selectedItem = item;
    }
	
	public void removeSelection() {
		selectedItem = null;
	}
	
	public Item getSelectedItem() {
		return this.selectedItem;
	}

}
