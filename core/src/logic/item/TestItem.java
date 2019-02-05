package logic.item;

import logic.item.level.SoldierLevel;
import logic.item.level.TowerLevel;

public class TestItem {
	public static void main(String[] args) {
		int rows = 2;
		int columns = 3;
		String[][] board= new String[rows][columns];
		System.out.println(board[0][0]);
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				board[i][j] = "(i:"+String.valueOf(i)+" j:"+String.valueOf(j)+")";
			}
		}
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j<columns; j++) {
				System.out.print(board[i][j] +" ");
			}
			System.out.println();
		}
	}
}
