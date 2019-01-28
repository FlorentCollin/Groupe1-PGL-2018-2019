package logic.board;

import logic.board.cell.Cell;
import logic.naturalDisasters.NaturalDisastersController;
import logic.player.Player;

import java.util.ArrayList;

public class Board{
	private Cell[][] board;
	private int columns, rows;
	private Player[] players;
	private ArrayList<District> districts;
	private NaturalDisastersController naturalDisastersController;

	public Board(int columns, int rows, Player[] players, NaturalDisastersController naturalDisastersController){
		this.columns = columns;
		this.rows = rows;
		board = new Cell[rows][columns];
		this.players = players;
		this.naturalDisastersController = naturalDisastersController;
	}
}
