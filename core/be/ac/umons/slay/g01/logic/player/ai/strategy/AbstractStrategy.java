package ac.umons.slay.g01.logic.player.ai.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.District;
import ac.umons.slay.g01.logic.board.cell.Cell;
import ac.umons.slay.g01.logic.item.Soldier;
import ac.umons.slay.g01.logic.item.Tree;
import ac.umons.slay.g01.logic.item.level.SoldierLevel;
import ac.umons.slay.g01.logic.player.Player;

public abstract class AbstractStrategy implements Strategy {
	protected transient HashMap<District, Integer> visitedDistricts;
	protected transient ArrayList<Cell> soldierCells;
	protected transient Random rand;

	public AbstractStrategy() {
		soldierCells = new ArrayList<>();
		rand = new Random();
		visitedDistricts = new HashMap<>();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Permet de récupérer une cellule contenant un soldat améliorable
	 * @param cell la cellule de départ
	 * @param possibleMoves les déplacements possibles pour cette cellule
	 * @return null ou une cellule contenant un soldat à améliorer
	 */
	protected Cell improveSoldier(Cell cell, ArrayList<Cell> possibleMoves) {
		Player currentPlayer = cell.getDistrict().getPlayer();
		Soldier currentSoldier = (Soldier) cell.getItem();
		if(currentSoldier.getLevel().isNotMax()) {
			Soldier superiorSoldier = new Soldier(SoldierLevel.values()[currentSoldier.getLevel().getIndex()+1]);
			int newSalary = superiorSoldier.getSalary();
			if(newSalary * 3 <= cell.getDistrict().getGold()) {
				for(Cell possible : possibleMoves) {
					if(possible.getItem() instanceof Soldier && possible.getDistrict().getPlayer() == currentPlayer) {
						return possible;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Permet de récupérer une cellule contenant un enemi
	 * @param cell la cellule de départ
	 * @param possibleMoves les déplacements possibles
	 * @return null ou une cellule contenant un enemi
	 */
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

	/**
	 * Permet de récupérer une cellule contenant un arbre
	 * @param cell la cellule de départ
	 * @param possibleMoves les déplacement possible
	 * @return null ou une cellule contenant un arbre
	 */
	protected Cell cutTrees(Cell cell, ArrayList<Cell> possibleMoves) {
		Player currentPlayer = cell.getDistrict().getPlayer();
		for(Cell possible : possibleMoves) {
			if(possible.getItem() instanceof Tree && possible.getDistrict() != null && possible.getDistrict().getPlayer() == currentPlayer) {
				return possible;
			}
		}
		return null;
	}

	/**
	 * Permet de connaître la somme des salaires des soldats d'un district
	 * @param cell la cellule de départ
	 * @return la somme des salaires
	 */
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
	
	/**
	 * Permet de connaître le nombre de cellule d'un district
	 * @param cell la cellule de départ
	 * @return la taille du district de la cellule
	 */
	protected int nCells(Cell cell) {
		return cell.getDistrict().getCells().size();
	}
	
	/**
	 * Permet de déplacer un soldat
	 * @param fromCell la cellule de départ
	 * @param toCell la cellule d'arrivé
	 * @param board le plateau
	 */
	protected void move(Cell fromCell, Cell toCell, Board board) {
		board.setSelectedCell(fromCell);
		board.play(toCell);
	}

	/**
	 * Permet d'acheter un soldat
	 * @param fromCell la cellule de départ
	 * @param toCell la cellule d'arrivé
	 * @param board le plateau
	 */
	protected void buy(Cell fromCell, Cell toCell, Board board) {
		board.setSelectedCell(fromCell);
		if(toCell.getDistrict() != null)
			board.play(toCell);
	}

	/**
	 * Permet de récupérer le meilleur soldat achetable
	 * @param district le district avec lequel acheter
	 * @return le meilleur soldat ou null
	 */
	protected Soldier bestSoldier(District district) {
		int gold = district.getGold();
		SoldierLevel level = null;
		for(SoldierLevel l : SoldierLevel.values()) {
			if(gold > (l.getPrice() + l.getSalary())) {
				level = l;
			}
		}
		if(level != null) {
			Soldier soldier = new Soldier(level);
			return soldier;
		}
		return null;
	}

	/**
	 * Permet de récupérer les cellules contenant des soldats
	 * @param districts la liste des districts
	 * @return les cellules des soldats
	 */
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
	
	/**
	 * Permet de récupérer un district
	 * @param districts la liste des districts
	 * @return un district
	 */
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

	/**
	 * Permet de récupérer une cellule aléatoire
	 * @param cell la cellule de départ
	 * @param possibleMoves les déplacements possible
	 * @return null ou une cellule
	 */
	protected Cell randomCell(Cell cell, ArrayList<Cell> possibleMoves) {
		int size = possibleMoves.size();
		if(size > 0) {
			int r = rand.nextInt(size);
			return possibleMoves.get(r);
		}
		return null;
	}

	@Override
	public void play(Board board, ArrayList<District> districts) {

	}

}