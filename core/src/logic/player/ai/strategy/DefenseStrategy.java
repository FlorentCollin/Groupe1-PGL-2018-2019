package logic.player.ai.strategy;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.item.Capital;
import logic.item.Soldier;
import logic.player.Player;

public class DefenseStrategy extends AbstractStrategy{

	@Override
	public void play(Board board, ArrayList<District> districts) {
		visitedDistricts.clear();
		ArrayList<Cell> cellToDefend;
		ArrayList<Cell> inactifSoldiers = inactifSoldiers(board, districts);
		ArrayList<Cell> possibleMoves;
		Cell choice;
		for(Cell soldier : inactifSoldiers) {
			possibleMoves = board.possibleMove(soldier);
			cellToDefend = cellToDefend(board, districts);
			choice = killEnemy(soldier, possibleMoves);
			if(choice != null) {
				move(soldier, choice, board);
			}
			else {
				choice = cutTrees(soldier, possibleMoves);
				if(choice != null) {
					move(soldier, choice, board);
				}
				else {
					choice = joinCellToDefend(cellToDefend,possibleMoves);
					if(choice != null) {
						move(soldier, choice, board);
					}
					else {
						choice = randomCell(soldier, possibleMoves);
						if(choice != null) {
							move(soldier, choice, board);
						}
					}
				}
			}
		}

		District district;
		Soldier newSoldier;
		while((district = getDistrict(districts)) != null) {
			newSoldier = bestSoldier(district);
			if(newSoldier != null) {
				cellToDefend = cellToDefend(board, districts);
				board.setSelectedCell(district.getCapital());
				board.setShopItem(newSoldier);
				possibleMoves = board.possibleMove(district);
				choice = cutTrees(district.getCapital(), possibleMoves);
				if(choice != null) {
					buy(district.getCapital(), choice, board);
				}
				else {
					choice = killEnemy(district.getCapital(), possibleMoves);
					if(choice != null) {
						buy(district.getCapital(), choice, board);
					}
					else {
						choice = joinCellToDefend(cellToDefend, possibleMoves);
						if(choice != null) {
							buy(district.getCapital(), choice, board);
						}
						else {
							choice = randomCell(district.getCapital(), possibleMoves);
							if(choice != null) {
								buy(district.getCapital(), choice, board);
							}
						}
					}
				}
			}
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
		toDefend.addAll(checkDefenseOfCapitals(board, districts));
		return toDefend;
	}

	private ArrayList<Cell> checkDefenseOfCapitals(Board board, ArrayList<District> districts){
		ArrayList<Cell> defenseOfCapitals = new ArrayList<>();
		for(District district : districts) {
			for(Cell neighbour : board.getNeighbors(district.getCapital())) {
				if(neighbour.getItem() == null) {
					defenseOfCapitals.add(neighbour);
				}
			}
		}
		return defenseOfCapitals;
	}

	private ArrayList<Cell> inactifSoldiers(Board board, ArrayList<District> districts){
		ArrayList<Cell> inactifSoldiers = new ArrayList<>();
		for(District district : districts) {
			for(Cell c : district.getCells()) {
				if(c.getItem() != null && c.getItem().isMovable()) {
					if(!defend(c, board)) {
						inactifSoldiers.add(c);
					}
				}
			}
		}
		return inactifSoldiers;
	}

	private boolean defend(Cell cell, Board board) {
		Player currentPlayer = cell.getDistrict().getPlayer();
		District neighbourDistrict;
		for(Cell neighbour : board.getNeighbors(cell)) {
			neighbourDistrict = neighbour.getDistrict();
			if((neighbourDistrict != null && neighbourDistrict.getPlayer() != currentPlayer) || (neighbour == cell.getDistrict().getCapital())) {
				return true;
			}
		}
		return false;
	}

	private Cell joinCellToDefend(ArrayList<Cell> toDefend, ArrayList<Cell> possibleMove) {
		for(Cell cell : possibleMove) {
			if(toDefend.indexOf(cell) > -1) {
				return cell;
			}
		}
		return null;
	}
}