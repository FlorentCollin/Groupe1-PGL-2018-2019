package logic.naturalDisasters;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.board.cell.DroughtCell;
import logic.board.cell.LandCell;
import logic.item.Capital;
import logic.item.Item;

public class Drought extends NaturalDisasters{

	public Drought(Board board) {
		super(board);
		setMaxAffectedCells(10);
	}
	
	
	private void drought() {
		nAffectedCells = 0;
		affectedCells.clear();
		Cell cell = getAnyCell();
		if(cell instanceof LandCell) {
			droughtFrom(cell);
		}
		saveChanges();
	}
	
	private void droughtFrom(Cell cell) {
		nAffectedCells ++;
		affectedCells.add(cell);
		//Récupération des info de cell
		Item item = cell.getItem();
		District district = cell.getDistrict();
		//Suppression de la cellule du district
		if(district != null) {
			district.removeCell(cell);
		}
		//Réinitialisation de cell entant que DroughtCell avec les anciennes données
		cell = new DroughtCell(cell.getX(), cell.getY());
		cell.setItem(item);
		cell.setDistrict(district);
		//Ajout de la cellule au district
		if(district != null) {
			district.addCell(cell);			
		}
		//Mise à jour de la cellule contenant la capital du district
		if(item instanceof Capital) {
			district.addCapital(cell);
		}
		board.setCell(cell);
		board.addModification(cell);
		if(nAffectedCells < getMaxAffectedCells() && mustHappen(50)) {
			Cell c = getOneFrom(board.getNeighbors(cell));
			if(c != null) {
				droughtFrom(c);
			}
		}
	}
	
	@Override
	public void play() {
		cancel();
		if(mustHappen(getProba())) {
			drought();
		}
	}
}
