package logic.player.ai.strategy;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.item.Soldier;

public class RandomStrategy extends AbstractStrategy{
	
	public RandomStrategy() {
		super();
	}
	
	@Override
	public void play(Board board, ArrayList<District> districts) {
		visitedDistricts.clear();
		Cell cutTree;
		Cell randomCell;
		ArrayList<Cell> possibleMoves;
		for(Cell cell : soldierCells(districts)) {
			possibleMoves = board.possibleMove(cell);
			cutTree = cutTrees(cell, possibleMoves);
			if(cutTree != null) {
				move(cell, cutTree, board);
			}
			else {
				randomCell = randomCell(cell, possibleMoves);
				if(randomCell != null) {
					move(cell, randomCell, board);
				}
			}
		}
		
		District district;
		Soldier newSoldier;
		while((district = getDistrict(districts)) != null) {
			for(int i=0; i<5; i++) {
				newSoldier = bestSoldier(district);
				if(newSoldier != null) {
					board.setSelectedCell(district.getCapital());
					board.setShopItem(newSoldier);
					possibleMoves = board.possibleMove(district);
					cutTree = cutTrees(district.getCapital(), possibleMoves);
					if(cutTree != null) {
						buy(district.getCapital(), cutTree, board);
					}
					else {
						randomCell = randomCell(district.getCapital(), possibleMoves);
						if(randomCell != null) {
							buy(district.getCapital(), randomCell, board);
						}
					}
				}
			}
		}
	}
	
	
}
