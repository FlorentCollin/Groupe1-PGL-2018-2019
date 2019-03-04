package logic.player.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.item.Item;
import logic.item.Soldier;
import logic.item.Tree;
import logic.item.level.SoldierLevel;
import logic.player.Player;

public abstract class AbstractStrategy implements Strategy {
	protected HashMap<District, Integer> visitedDistricts;
	protected ArrayList<Cell> soldierCells;
	protected Random rand;
	
	public AbstractStrategy() {
		soldierCells = new ArrayList<>();
		rand = new Random();
		visitedDistricts = new HashMap<>();
		// TODO Auto-generated constructor stub
	}
	
	protected Cell improveSoldier(Cell cell, ArrayList<Cell> possibleMoves) {
		Player currentPlayer = cell.getDistrict().getPlayer();
		Soldier currentSoldier = (Soldier) cell.getItem();
		Soldier superiorSoldier = new Soldier(SoldierLevel.values()[currentSoldier.getLevel().getIndex()+1]);
		int newSalary = superiorSoldier.getSalary();
		for(Cell possible : possibleMoves) {
			if(newSalary * 3 > cell.getDistrict().getGold()) {
				break;
			}
			if(possible.getItem() instanceof Soldier && possible.getDistrict().getPlayer() != currentPlayer) {
				return possible;
			}
		}
		return null;
	}
	
	protected Cell killEnemy(Cell cell, ArrayList<Cell> possibleMoves) {
		Player currentPlayer = cell.getDistrict().getPlayer();
		Player enemy;
		for(Cell possible : possibleMoves) {
			enemy = null;
			if(possible.getDistrict() != null) {
				enemy = possible.getDistrict().getPlayer();
			}
			if(possible.getItem() != null && enemy != null && enemy != currentPlayer) {
				return possible;
			}
		}
		return null;
	}
	
	protected Cell cutTrees(Cell cell, ArrayList<Cell> possibleMoves) {
		for(Cell possible : possibleMoves) {
			if(possible.getItem() instanceof Tree) {
				return possible;
			}
		}
		return null;
	}
	
	protected int sommeSalary(Cell cell) {
		District district = cell.getDistrict();
		int sold = 0;
		for(Cell c : district.getCells()) {
			if(c.getItem() != null) {
				sold += c.getItem().getSalary();
			}
		}
		return sold;
	}
	
	protected int nCells(Cell cell) {
		return cell.getDistrict().getCells().size();
	}
	
	protected void move(Cell fromCell, Cell toCell, Board board) {
		board.setSelectedCell(fromCell);
		board.play(toCell);
	}
	
	public void move(Cell fromCell, Board board) {
		Cell toCell = getChoice(fromCell, board);
		board.setSelectedCell(fromCell);
		board.play(toCell);
	}
	
	protected void buy(Cell fromCell, Cell toCell, Board board) {
		board.setSelectedCell(fromCell);
		SoldierLevel level = bestSoldier(fromCell);
		if(level != null) {
			board.getShop().setSelectedItem(new Soldier(level), fromCell.getDistrict());
			board.play(toCell);
		}
	}
	
	private SoldierLevel bestSoldier(Cell cell) {
		int gold = cell.getDistrict().getGold();
		SoldierLevel level = null;
		for(SoldierLevel l : SoldierLevel.values()) {
			if(gold > (l.getPrice() + l.getSalary()) * 3) {
				level = l;
			}
		}
		return level;
	}

	public void buy(Cell cell, Board board) {
		board.setSelectedCell(cell);
		District district = cell.getDistrict();
		Cell toCell;
		if(district.getGold() > SoldierLevel.level1.getPrice()+SoldierLevel.level1.getSalary()) {
			Item soldier = new Soldier(SoldierLevel.level1);
			board.getShop().setSelectedItem(soldier, district);
			toCell = getChoice(district, board);
			board.play(toCell);
		}
	}
	
	protected Cell getChoice(Cell cell, Board board) {
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
	
	protected Cell getChoice(District district, Board board) {
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
	
	protected ArrayList<Cell> soldierCells(ArrayList<District> districts){
		ArrayList<Cell> soldierCells = new ArrayList<>();
		for(District district : districts) {
			for(Cell c : district.getCells()) {
				if(c.getItem() != null && c.getItem().isMovable() && c.getItem().canMove()) {
					soldierCells.add(c);
				}
			}
		}
		return soldierCells;
	}
	
	protected District getDistrict(ArrayList<District> districts) {
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
	
	protected Cell randomCell(Cell cell, ArrayList<Cell> possibleMoves) {
		int size = possibleMoves.size();
		int r = rand.nextInt(size);
		return possibleMoves.get(r);
	}

	@Override
	public void play(Board board, ArrayList<District> districts) {
		// TODO Auto-generated method stub

	}

}
