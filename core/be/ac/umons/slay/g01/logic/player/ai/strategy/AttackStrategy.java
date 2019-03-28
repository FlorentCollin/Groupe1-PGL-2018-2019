package ac.umons.slay.g01.logic.player.ai.strategy;

import java.util.ArrayList;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.District;
import ac.umons.slay.g01.logic.board.cell.Cell;
import ac.umons.slay.g01.logic.item.Soldier;

public class AttackStrategy extends AbstractStrategy{
	@Override
	public void play(Board board, ArrayList<District> districts) {
		visitedDistricts.clear();
		Cell choice;
		ArrayList<Cell> possibleMoves;

		for(Cell cell : soldierCells(districts)) {
			board.setSelectedCell(cell);
			possibleMoves = board.possibleMove(cell);
			choice = cutTrees(cell, possibleMoves);
			if(choice != null) {
				move(cell, choice, board);
			}
			else {
				choice = killEnemy(cell, possibleMoves);
				if(choice != null) {
					move(cell, choice, board);
				}
				else {
					choice = improveSoldier(cell, possibleMoves);
					if(choice != null) {
						move(cell, choice, board);
					}
					else {
						choice = randomCell(cell, possibleMoves);
						if(choice != null) {
							move(cell, choice, board);
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