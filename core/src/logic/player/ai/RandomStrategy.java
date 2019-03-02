package logic.player.ai;

import java.util.ArrayList;
import java.util.HashMap;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.item.Item;
import logic.item.Soldier;
import logic.item.Tree;
import logic.item.level.SoldierLevel;

public class RandomStrategy extends AbstractStrategy{
	private HashMap<District, Integer> visitedDistricts = new HashMap<>();
	
	public RandomStrategy() {
		
	}
	
	public void move(Cell fromCell, Board board) {
		Cell toCell = getChoice(fromCell, board);
		board.setSelectedCell(fromCell);
		board.play(toCell);
	}

	public void buy(Cell cell, Board board) {
		board.setSelectedCell(cell);
		District district = cell.getDistrict();
		if(district.getGold() > SoldierLevel.level1.getPrice()+SoldierLevel.level1.getSalary()) {
			Item soldier = new Soldier(SoldierLevel.level1);
			board.getShop().setSelectedItem(soldier, district);
			Cell toCell = getChoice(district, board);
			board.play(toCell);
		}
	}

	@Override
	public void play(Board board, ArrayList<District> districts) {
		visitedDistricts.clear();
		for(Cell cell : soldierCells(districts)) {
			move(cell, board);
		}
		
		District district;
		while((district = getDistrict(districts)) != null) {
			Cell cell = district.getCells().get(0);
			buy(cell, board);
		}
	}
	
	private Cell getChoice(Cell cell, Board board) {
		Cell treeChoice = null;
		Cell improveChoice = null;
		Cell enemyChoice = null;
		for(Cell c : board.possibleMove(cell)) {
			if(c.getItem() instanceof Tree && treeChoice == null) {
				treeChoice = c;
			}
			else if(c.getItem() != null && c.getItem().getClass().isInstance(cell.getItem()) && improveChoice == null) {
				if(cell.getDistrict().getGold() - ((SoldierLevel)cell.getItem().getLevel()).getSalary()*10 > 0){
					improveChoice = c;
				}
			}
			else if(c.getDistrict() != null && c.getDistrict().getPlayer() != cell.getDistrict().getPlayer() && enemyChoice == null) {
				enemyChoice = c;
			}
		}
		if(treeChoice != null) {
			return treeChoice;
		}
		else if(enemyChoice != null) {
			return enemyChoice;
		}
		else if(improveChoice != null) {
			return improveChoice;
		}
		return board.possibleMove(cell).get(rand.nextInt(board.possibleMove(cell).size()));
	}
	
	private Cell getChoice(District district, Board board) {
		Cell treeChoice = null;
		Cell emptyChoice = null;
		for(Cell c : board.possibleMove(district)) {
			if(c.getDistrict() == null) {
				emptyChoice = c;
			}
			else if(c.getItem() instanceof Tree) {
				treeChoice = c;
			}
		}
		if(treeChoice != null) {
			return treeChoice;
		}
		return emptyChoice;
	}
	
	private ArrayList<Cell> soldierCells(ArrayList<District> districts){
		ArrayList<Cell> soldierCells = new ArrayList<>();
		for(District district : districts) {
			for(Cell c : district.getCells()) {
				if(c.getItem() != null && c.getItem().isMovable()) {
					soldierCells.add(c);
				}
			}
		}
		return soldierCells;
	}
	
	private District getDistrict(ArrayList<District> districts) {
		for(int i=0; i<districts.size(); i++) {
			if(! visitedDistricts.containsKey(districts.get(i))) {
				visitedDistricts.put(districts.get(i), districts.get(i).getCells().size());
				return districts.get(i);
			}
			else {
				if(visitedDistricts.get(districts.get(i)) != districts.get(i).getCells().size()) {
					visitedDistricts.put(districts.get(i), districts.get(i).getCells().size());
					return districts.get(i);
				}
			}
		}
		return null;
	}
}
