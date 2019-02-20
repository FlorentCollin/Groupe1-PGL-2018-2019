
import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.item.Capital;
import logic.item.Soldier;
import logic.item.level.SoldierLevel;
import logic.naturalDisasters.NaturalDisastersController;
import logic.player.Player;
import logic.shop.Shop;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class BoardTest {
	Board board;
	Player p1, p2;
	Shop shop;
	District district, district2;
	
	@Before
	public void init() {
		int rows = 5;
		int columns = 5;
		p1 = new Player();
		p2 = new Player();
		Player[] players = new Player[2];
		players[0] = p1;
		players[1] = p2;
		NaturalDisastersController naturalDisastersController = new NaturalDisastersController();
		shop = new Shop();
		board = new Board(rows,columns, players, naturalDisastersController, shop);
		district = new District(p1);
		district2 = new District(p2);
		district.setGold(10000);
		district2.setGold(10000);
		board.addDistrict(district);
		board.addDistrict(district2);
		for(int i=0; i<2; i++) {
			for(int j=0; j<2; j++) {
				district.addCell(board.getCell(i, j));
				board.getCell(i, j).setDistrict(district);
			}
		}
		for(int i = 2; i<4; i++) {
			for(int j = 2; j<4; j++) {
				district2.addCell(board.getCell(i, j));
				board.getCell(i, j).setDistrict(district2);
			}
		}
	}
	
	
	@Test
	public void testPlaceNewItemOnFreeTerritory() {
		board.setSelectedCell(board.getCell(1,1));
		Soldier s = new Soldier(p1);
		shop.setSelectedItem(s, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0,2));
		assertTrue(board.getCell(0, 2).getItem() == s);
		assertTrue(board.getCell(0, 2).getDistrict() == board.getCell(1, 1).getDistrict());
	}
	
	@Test 
	public void testPlaceNewItemOnOwnTerritory() {
		//Test pour une cellule vide
		Soldier soldier = new Soldier(p1);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(soldier, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		assertTrue(board.getCell(0, 0).getItem() == soldier);
		//Test pour une cellule contenant un autre soldat
		Soldier s2 = new Soldier(p1);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(s2, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		assertTrue(board.getCell(0, 0).getItem() == soldier);
		//Test pour une cellule contenant un item autre qu'un soldat
		Capital capital = new Capital();
		board.getCell(1, 0).setItem(capital);
		shop.setSelectedItem(s2, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(1, 0));
		assertTrue(board.getCell(1, 0).getItem() == capital);
	}
	
	@Test
	public void testFusion() {
		Soldier soldier = new Soldier(p1);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(soldier, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		Soldier soldier2 = new Soldier(p1);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(soldier2, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		assertTrue(board.getCell(0, 0).getItem().getLevel() == SoldierLevel.level2);
	}
	
	@Test
	public void testNotFusion() {
		Soldier soldier = new Soldier(p1, SoldierLevel.level2);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(soldier, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		Soldier soldier2 = new Soldier(p1);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(soldier2, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		assertTrue(board.getCell(0, 0).getItem() == soldier);
	}
	
	@Test
	public void testMoveFusion() {
		//Ajout d'un soldat en (0,0)
		Soldier firstSoldier = new Soldier(p1);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(firstSoldier, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		//Ajout d'un soldat en (0,1)
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(new Soldier(p1), board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 1));
		//Déplacement
		board.setSelectedCell(board.getCell(0, 1));
		board.nextPlayer();
		board.nextPlayer();
		board.move(board.getCell(0, 0));
		assertTrue(board.getCell(0, 0).getItem().getLevel()==SoldierLevel.level2);
		assertTrue(board.getCell(0, 0).getItem() == firstSoldier);
		assertTrue(board.getCell(0, 1).getItem() == null);
	}
	
	@Test
	public void testPlaceNewItemOnEnemyTerritory() {
		//Ajout de cellules au district enemi
		board.getCell(1, 2).setDistrict(board.getCell(2, 2).getDistrict());
		board.getCell(2, 1).setDistrict(board.getCell(2, 2).getDistrict());
		board.getCell(2, 2).getDistrict().addCell(board.getCell(1, 2));
		board.getCell(2, 2).getDistrict().addCell(board.getCell(2, 1));
		//Ajout d'items ennemis
		board.getCell(2, 2).setItem(new Soldier(p2));
		board.getCell(1, 2).setItem(new Capital());
		Soldier highSoldier = new Soldier(p2, SoldierLevel.level2);
		board.getCell(2, 1).setItem(highSoldier);
		//Place sur (2,2)
		board.setSelectedCell(board.getCell(1, 1));
		Soldier s1 = new Soldier(p1);
		shop.setSelectedItem(s1, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(2, 2));
		//Place sur (1,2)
		board.setSelectedCell(board.getCell(1, 1));
		Soldier s2 = new Soldier(p1);
		shop.setSelectedItem(s2, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(1, 2));
		//Place sur (2,1)
		board.setSelectedCell(board.getCell(1, 1));
		Soldier s3 = new Soldier(p1);
		shop.setSelectedItem(s3, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(2, 1));
		
		assertTrue(board.getCell(2, 2).getDistrict() == board.getCell(1, 1).getDistrict());
		assertTrue(board.getCell(2, 2).getItem() == s1);
		assertTrue(board.getCell(1, 2).getDistrict() == board.getCell(1, 1).getDistrict());
		assertTrue(board.getCell(1, 2).getItem() == s2);
		assertTrue(board.getCell(2, 1).getDistrict() != board.getCell(1, 1).getDistrict());
		assertTrue(board.getCell(2, 1).getItem() == highSoldier);
	}
	
	@Test
	public void testPossibleMoveForDistrict() {
		board.setSelectedCell(board.getCell(1, 1));
		ArrayList<Cell> possibleMoves = board.possibleMove(board.getSelectedCell().getDistrict());
		ArrayList<Cell> check = new ArrayList<Cell>();
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				check.add(board.getCell(i, j));
			}
		}
		assertTrue(check.containsAll(possibleMoves));
		assertTrue(possibleMoves.containsAll(check));
	}
	
	@Test
	public void testPossibleMoveForCell() {
		Cell cell = board.getCell(1, 1);
		cell.setItem(new Soldier(p1));
		ArrayList<Cell> possibleMoves = board.possibleMove(cell);
		ArrayList<Cell> check = new ArrayList<Cell>();
		check.add(board.getCell(0, 1));
		check.add(board.getCell(1, 0));
		check.add(board.getCell(1, 2));
		check.add(board.getCell(2, 2));
		check.add(board.getCell(2, 1));
		check.add(board.getCell(2, 0));
		assertTrue(possibleMoves.containsAll(check));
		assertTrue(check.containsAll(possibleMoves));
		possibleMoves = board.possibleMove(board.getCell(2, 2));
		check.clear();
		check.add(board.getCell(1, 2));
		check.add(board.getCell(1, 3));
		check.add(board.getCell(2, 3));
		check.add(board.getCell(3, 2));
		check.add(board.getCell(2, 1));
		check.add(board.getCell(1, 1));
		assertTrue(possibleMoves.containsAll(check));
		assertTrue(check.containsAll(possibleMoves));
	}
	
	@Test
	public void testMoveOnFreeTerritory() {
		Soldier s = new Soldier(p1);
		board.getCell(1, 1).setItem(s);
		board.setSelectedCell(board.getCell(1, 1));
		board.move(board.getCell(0, 4));
		assertTrue(board.getCell(0, 4).getDistrict() == null);
		board.setSelectedCell(board.getCell(1, 1));
		board.move(board.getCell(1, 2));
		assertTrue(board.getCell(1, 2).getDistrict() == board.getCell(1, 1).getDistrict());
		assertTrue(board.getCell(1, 2).getItem() == s);
	}
	
	@Test
	public void testMoveOnOwnTerritory() {
		Soldier s = new Soldier(p1);
		board.getCell(1, 0).setItem(s);
		board.getCell(0, 0).setItem(new Capital());
		Soldier s2 = new Soldier(p1, SoldierLevel.level2);
		board.getCell(1, 1).setItem(s2);
		board.setSelectedCell(board.getCell(1, 0));
		board.move(board.getCell(0, 0));
		board.setSelectedCell(board.getCell(1, 0));
		board.move(board.getCell(1, 1));
		board.setSelectedCell(board.getCell(1, 0));
		board.move(board.getCell(0, 1));
		assertTrue(board.getCell(0, 0).getItem() instanceof Capital);
		assertTrue(board.getCell(1, 1).getItem() == s2);
		assertTrue(board.getCell(0, 1).getItem() == s);
		assertTrue(board.getCell(1, 0).getItem() == null);
	}
	
	@Test
	public void testMoveOnEnemyTerritory() {
		board.getCell(1, 2).setDistrict(board.getCell(2, 2).getDistrict());
		board.getCell(2, 1).setDistrict(board.getCell(2, 2).getDistrict());
		Soldier s = new Soldier(p1);
		Soldier s2 = new Soldier(p1);
		board.getCell(1, 1).setItem(s);
		board.getCell(0, 1).setItem(s2);
		board.getCell(2, 1).setItem(new Capital());
		board.getCell(2, 2).setItem(new Soldier(p2, SoldierLevel.level2));
		board.getCell(1, 2).setItem(new Soldier(p2));
		board.setSelectedCell(board.getCell(1, 1));
		board.move(board.getCell(2, 2));
		board.setSelectedCell(board.getCell(1, 1));
		board.move(board.getCell(2, 1));
		board.setSelectedCell(board.getCell(0, 1));
		board.move(board.getCell(1, 2));
		assertTrue(board.getCell(2, 1).getDistrict() == board.getCell(1, 1).getDistrict());
		assertTrue(board.getCell(2, 1).getItem() == s);
		assertTrue(board.getCell(1, 2).getDistrict() == board.getCell(1, 1).getDistrict());
		assertTrue(board.getCell(1, 2).getItem() == s2);
		assertTrue(board.getCell(2, 2).getDistrict() == board.getCell(2, 3).getDistrict());
	}
	
	@Test
	public void testMerge() {
		district2.setPlayer(p1);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(new Soldier(p1), board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(1, 2));
		assertTrue(board.getCell(2, 2).getDistrict() == board.getCell(1, 1).getDistrict());
	}
	
}
