package logic.board;

import java.util.ArrayList;

import logic.board.cell.Cell;
import logic.item.Item;
import logic.naturalDisasters.NaturalDisastersController;
import logic.player.Player;

public class Board{
	private Cell[][] board;
	private int columns, rows;
	private Player[] players;
	private ArrayList<District> districts;
	private NaturalDisastersController naturalDisastersController;
	//utiliser dictionnaire {cell : district} pour connaître plus vite le district d'une cellule
	
	public Board(int columns, int rows, Player[] players, NaturalDisastersController naturalDisastersController){
		this.columns = columns;
		this.rows = rows;
		board = new Cell[rows][columns];
		this.players = players;
		this.naturalDisastersController = naturalDisastersController;
		this.districts = new ArrayList<District>();
	}
	
	/*
	 * Permet de placer un item sur une cellule du plateau
	 * @param item l'item à placer
	 * @param i la position en x que doit prendre l'item
	 * @param j la position en y que doit prendre l'item
	 * */
	public void place(Item item, int i, int j){
		if(i >= 0 && i < rows && j >= 0 && j < columns) {
			Cell cell = board[i][j];
			District district = getDistrict(cell);
			
		}
	
	}
	
	/*
	 * Permet d'obtenir le district d'une cellule
	 * @param cell la cellule dont on souhaite connaître le district
	 * @return le district de la celluel
	 * */
	private District getDistrict(Cell cell) {
		//D'où la proposition du dictionnaire
		for(District district : districts) {
			for(Cell c : district.getCells()) {
				if(c == cell) {
					return district;
				}
			}
		}
		return null;
	}
	
	/*
	 * Peremet de déplacer un item d'une cellule à l'autre
	 * @param fromCell la cellule de départ
	 * @param toCell la cellule de destination
	 * */
	public void move(Cell fromCell, Cell toCell) {
		
	}
	
	/*
	 * Permet de connaître les mouvements possibles d'un item
	 * @param cell la cellule où se trouve l'item
	 * @return les cellules sur lesquelles peut se déplacer l'item
	 * */
	public Cell[] possibleMove(Cell cell) {
		return null;
	}
}
