package logic.player.ai;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.item.Soldier;
import logic.player.Player;

public class DefenseStrategy extends AbstractStrategy{
	
	@Override
	public void play(Board board, ArrayList<District> districts) {
		ArrayList<Cell> cellToDefend = cellToDefend(board, districts);
		ArrayList<Cell> inactifSoldiers = inactifSoldiers(board, districts);
		Cell toCell;
		for(Cell soldier : inactifSoldiers) {
			toCell = joinCellToDefend(cellToDefend, board.possibleMove(soldier));
			
		}

	}
	
	private ArrayList<Cell> cellToDefend(Board board, ArrayList<District> districts){
		Player ai = districts.get(0).getPlayer();
		ArrayList<Cell> toDefend = new ArrayList<>();
		for(District district : districts) {
			for(Cell c : district.getCells()) {
				if(c.getItem() == null) {
					for(Cell neighbourCell : board.getNeighbors(c)) {
						if(neighbourCell.getDistrict() != null 
								&& neighbourCell.getDistrict().getPlayer() != ai
								&& toDefend.indexOf(c) == -1) {
							toDefend.add(c);
						}
					}
				}
			}
		}
		return toDefend;
	}
	
	private ArrayList<Cell> inactifSoldiers(Board board, ArrayList<District> districts){
		Player ai = districts.get(0).getPlayer();
		ArrayList<Cell> inactifSoldiers = new ArrayList<>();
		for(District district : districts) {
			for(Cell c : district.getCells()) {
				if(c.getItem() != null && c.getItem().isMovable()) {
					if(dontDefend(c, board)) {
						inactifSoldiers.add(c);
					}
				}
			}
		}
		return inactifSoldiers;
	}
	
	private boolean dontDefend(Cell cell, Board board) {
		Player currentPlayer = cell.getDistrict().getPlayer();
		District neighbourDistrict;
		for(Cell neighbour : board.getNeighbors(cell)) {
			neighbourDistrict = neighbour.getDistrict();
			if(neighbourDistrict != null && neighbourDistrict.getPlayer() != currentPlayer) {
				return false;
			}
		}
		return true;
	}
	
	private Cell joinCellToDefend(ArrayList<Cell> toDefend, ArrayList<Cell> possibleMove) {
		for(Cell cell : possibleMove) {
			if(toDefend.indexOf(cell) != -1) {
				return cell;
			}
		}
		return null;
	}
}
