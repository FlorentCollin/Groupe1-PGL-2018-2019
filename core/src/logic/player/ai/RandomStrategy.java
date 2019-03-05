package logic.player.ai;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;

public class RandomStrategy extends AbstractStrategy{
	
	public RandomStrategy() {
		
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
				move(cell, randomCell, board);
			}
		}
		
		District district;
		while((district = getDistrict(districts)) != null) {
			for(int i=0; i<5; i++) {
				possibleMoves = board.possibleMove(district);
				cutTree = cutTrees(district.getCapital(), possibleMoves);
				if(cutTree != null) {
					buy(district.getCapital(), cutTree, board);
				}
				else {
					randomCell = randomCell(district.getCapital(), possibleMoves);
					buy(district.getCapital(), randomCell, board);
				}
			}
		}
	}
	
	
}
