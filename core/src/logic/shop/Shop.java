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
		if(selectedItem == null) {
			System.out.println("sheiBe item");
		}
		if(district == null) {
			System.out.println("sheiBe district");
		}
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
