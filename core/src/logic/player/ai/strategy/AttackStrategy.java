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
		Cell cutTree;
		Cell randomCell;
		ArrayList<Cell> possibleMoves;
		
		for(Cell cell : soldierCells(districts)) {
			possibleMoves = board.possibleMove(cell);
			cutTree = cutTrees(cell, possibleMoves);
			if(cutTree != null) {
				move(cell, cutTree, board);
			}
		}
	}
	
//	private Cell findEnemy(ArrayList<Cell> possibleMoves) {
//		for(Cell cell : possibleMoves) {
//			if(cell.getDistrict() != null && )
//		}
//	}
}
