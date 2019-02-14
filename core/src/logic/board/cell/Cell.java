package logic.board.cell;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import logic.board.District;
import logic.item.Item;

public class Cell extends TiledMapTileLayer.Cell {
	private Item item; // si null alors il n'y a pas d'item actuellement sur la cellule
	private transient District district; // si null alors n'appartient actuellement à aucun district
	private int numberOfAdjacentWaterCell; //?
	private boolean actifIncome; //?
	protected boolean accessible;

	public Cell() {
		
	}
	
	public Cell(District district) {
		this.district = district;
		// Pour n'appeler qu'une seule méthode
		district.addCell(this);
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	
	public Item getItem() {
		return this.item;
	}
	
	public void setDistrict(District district) {
		this.district = district;
	}
	
	public District getDistrict() {
		return this.district;
	}
	
	public void removeItem() {
		item = null;
	}
	
}
