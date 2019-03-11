package logic.player.ai.strategy;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.item.Soldier;
import logic.player.Player;

public class AttackStrategy extends AbstractStrategy{
	@Override
	public void play(Board board, ArrayList<District> districts) {
		visitedDistricts.clear();
		Cell choice;
		ArrayList<Cell> possibleMoves;

		for(Cell cell : soldierCells(districts)) {
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