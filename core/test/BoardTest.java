
import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.item.Capital;
import logic.item.Soldier;
import logic.item.level.SoldierLevel;
import logic.naturalDisasters.naturalDisasterscontroller.NaturalDisastersController;
import logic.player.Player;
import logic.shop.Shop;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;

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
		CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>();
		players.add(p1);
		players.add(p2);
		shop = new Shop();
		board = new Board(rows,columns, players, false, shop);
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
		Soldier s = new Soldier(SoldierLevel.level1);
		shop.setSelectedItem(s, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0,2));
		assertSame(board.getCell(0, 2).getItem(), s);
		assertSame(board.getCell(0, 2).getDistrict(), board.getCell(1, 1).getDistrict());
	}
	
	@Test
	public void testPlaceNewItemOnOwnTerritory() {
		//Test pour une cellule vide
		Soldier soldier = new Soldier(SoldierLevel.level1);
		district.addCapital(board.getCell(1,1));
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(soldier, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		assertSame(board.getCell(0, 0).getItem(), soldier);
		//Test pour une cellule contenant un autre soldat
		Soldier s2 = new Soldier(SoldierLevel.level1);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(s2, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		assertSame(board.getCell(0, 0).getItem(), soldier);
		//Test pour une cellule contenant un item autre qu'un soldat
		Capital capital = new Capital();
		board.getCell(1, 0).setItem(capital);
		shop.setSelectedItem(s2, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(1, 0));
		assertSame(board.getCell(1, 0).getItem(), capital);
	}

//	@Test
//	public void testFusion() {
//		Soldier soldier = new Soldier(p1);
//		board.setSelectedCell(board.getCell(1, 1));
//		shop.setSelectedItem(soldier, board.getSelectedCell().getDistrict());
//		board.placeNewItem(board.getCell(0, 0));
//		Soldier soldier2 = new Soldier(p1);
//		board.setSelectedCell(board.getCell(1, 1));
//		shop.setSelectedItem(soldier2, board.getSelectedCell().getDistrict());
//		board.placeNewItem(board.getCell(0, 0));
//		assertTrue(board.getCell(0, 0).getItem().getLevel() == SoldierLevel.level2);
//	}
//
//	@Test
//	public void testNotFusion() {
//		Soldier soldier = new Soldier(p1, SoldierLevel.level2);
//		board.setSelectedCell(board.getCell(1, 1));
//		shop.setSelectedItem(soldier, board.getSelectedCell().getDistrict());
//		board.placeNewItem(board.getCell(0, 0));
//		Soldier soldier2 = new Soldier(p1);
//		board.setSelectedCell(board.getCell(1, 1));
//		shop.setSelectedItem(soldier2, board.getSelectedCell().getDistrict());
//		board.placeNewItem(board.getCell(0, 0));
//		assertTrue(board.getCell(0, 0).getItem() == soldier);
//	}
//
//	@Test
//	public void testMoveFusion() {
//		//Ajout d'un soldat en (0,0)
//		Soldier firstSoldier = new Soldier(p1);
//		board.setSelectedCell(board.getCell(1, 1));
//		shop.setSelectedItem(firstSoldier, board.getSelectedCell().getDistrict());
//		board.placeNewItem(board.getCell(0, 0));
//		//Ajout d'un soldat en (0,1)
//		board.setSelectedCell(board.getCell(1, 1));
//		shop.setSelectedItem(new Soldier(p1), board.getSelectedCell().getDistrict());
//		board.placeNewItem(board.getCell(0, 1));
//		//Déplacement
//		board.setSelectedCell(board.getCell(0, 1));
//		board.nextPlayer();
//		board.nextPlayer();
//		board.move(board.getCell(0, 0));
//		assertTrue(board.getCell(0, 0).getItem().getLevel()==SoldierLevel.level2);
//		assertTrue(board.getCell(0, 0).getItem() == firstSoldier);
//		assertTrue(board.getCell(0, 1).getItem() == null);
//	}

//	@Test
//	public void testPlaceNewItemOnEnemyTerritory() {
//		district.addCapital(board.getCell(0,0));
//		district2.addCapital(board.getCell(4,4));
//		//Ajout de soldats ennemi
//		board.getCell(2,1).setDistrict(district2);
//		board.getCell(2,2).setDistrict(district2);
//		board.getCell(2,3).setDistrict(district2);
//		board.getCell(2,1).setItem(new Soldier(SoldierLevel.level1));
//		board.getCell(2,2).setItem(new Soldier(SoldierLevel.level2));
//		board.getCell(2,3).setItem(new Soldier(SoldierLevel.level3));
//		board.setSelectedCell(board.getCell(0,0));
//		board.setShopItem(new Soldier(SoldierLevel.level3));
//		board.play(board.getCell(2,1));
//		assertSame(board.getCell(2,1).getDistrict(), district);
//		//Ajout de cellules au district ennemi
//		district.addCapital(board.getCell(0,0));
//		district2.addCapital(board.getCell(4,4));
//		board.getCell(1, 2).setDistrict(board.getCell(2, 2).getDistrict());
//		board.getCell(2, 1).setDistrict(board.getCell(2, 2).getDistrict());
//		board.getCell(2, 2).getDistrict().addCell(board.getCell(1, 2));
//		board.getCell(2, 2).getDistrict().addCell(board.getCell(2, 1));
//		//Ajout d'items ennemis
//		board.getCell(2, 2).setItem(new Soldier(SoldierLevel.level1));
//		board.getCell(1, 2).setItem(new Capital());
//		Soldier highSoldier = new Soldier(SoldierLevel.level2);
//		board.getCell(2, 1).setItem(highSoldier);
//		//Place sur (2,2)
//		board.setSelectedCell(board.getCell(1, 1));
//		Soldier s1 = new Soldier(SoldierLevel.level1);
//		shop.setSelectedItem(s1, board.getSelectedCell().getDistrict());
//		board.play(board.getCell(2,2));
//		//Place sur (1,2)
//		board.setSelectedCell(board.getCell(1, 1));
//		Soldier s2 = new Soldier(SoldierLevel.level2);
//		shop.setSelectedItem(s2, board.getSelectedCell().getDistrict());
//		board.placeNewItem(board.getCell(1, 2));
//		//Place sur (2,1)
//		board.setSelectedCell(board.getCell(1, 1));
//		Soldier s3 = new Soldier(SoldierLevel.level3);
//		shop.setSelectedItem(s3, board.getSelectedCell().getDistrict());
//		board.placeNewItem(board.getCell(2, 1));
//
//		assertSame(board.getCell(2, 2).getDistrict(), board.getCell(1, 1).getDistrict());
//		assertSame(board.getCell(2, 2).getItem(), s1);
//		assertSame(board.getCell(1, 2).getDistrict(), board.getCell(1, 1).getDistrict());
//		assertSame(board.getCell(1, 2).getItem(), s2);
//		assertNotSame(board.getCell(2, 1).getDistrict(), board.getCell(1, 1).getDistrict());
//		assertSame(board.getCell(2, 1).getItem(), highSoldier);
//	}

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
		cell.setItem(new Soldier(SoldierLevel.level1));
		board.setSelectedCell(cell);
		ArrayList<Cell> possibleMoves = board.possibleMove(cell);
		ArrayList<Cell> check = new ArrayList<Cell>();
		check.add(board.getCell(0, 0));
		check.add(board.getCell(0, 1));
		check.add(board.getCell(0,2));
		check.add(board.getCell(1, 0));
		check.add(board.getCell(1, 2));
		check.add(board.getCell(2, 2));
		check.add(board.getCell(2, 1));
		check.add(board.getCell(2, 0));
		assertTrue(possibleMoves.containsAll(check));
		assertTrue(check.containsAll(possibleMoves));
		possibleMoves = board.possibleMove(board.getCell(2, 2));
		check.clear();
		check.add(board.getCell(2, 2));
		assertTrue(possibleMoves.containsAll(check));
		assertTrue(check.containsAll(possibleMoves));
	}

	@Test
	public void testMoveOnFreeTerritory() {
		Soldier s = new Soldier(SoldierLevel.level1);
		board.getCell(1, 1).setItem(s);
		board.setSelectedCell(board.getCell(1, 1));
		board.move(board.getCell(0, 4));
		assertNull(board.getCell(0, 4).getDistrict());
		board.setSelectedCell(board.getCell(1, 1));
		board.move(board.getCell(1, 2));
		assertSame(board.getCell(1, 2).getDistrict(), board.getCell(1, 1).getDistrict());
		assertSame(board.getCell(1, 2).getItem(), s);
	}

	@Test
	public void testMoveOnOwnTerritory() {
		Soldier s = new Soldier(SoldierLevel.level1);
		board.getCell(1, 0).setItem(s);
		district.addCapital(board.getCell(0,0));
		Soldier s2 = new Soldier(SoldierLevel.level2);
		board.getCell(1, 1).setItem(s2);
		board.setSelectedCell(board.getCell(1, 0));
		board.play(board.getCell(0, 0));
		board.setSelectedCell(board.getCell(1, 0));
		board.play(board.getCell(1, 1));
		board.setSelectedCell(board.getCell(1, 0));
		board.play(board.getCell(0, 1));
		assertTrue(board.getCell(0, 0).getItem() instanceof Capital);
		assertSame(board.getCell(1, 1).getItem(), s2);
		assertSame(board.getCell(0, 1).getItem(), s);
		assertNull(board.getCell(1, 0).getItem());
		assertSame(board.getCell(0, 1).getItem(), s);
	}

	@Test
	public void testMoveOnEnemyTerritory() {
		district.addCapital(board.getCell(0, 0));
		district2.addCapital(board.getCell(4,4));
		board.getCell(1, 2).setDistrict(board.getCell(2, 2).getDistrict());
		board.getCell(2, 1).setDistrict(board.getCell(2, 2).getDistrict());
		Soldier s = new Soldier(SoldierLevel.level1);
		Soldier s2 = new Soldier(SoldierLevel.level1);
		board.getCell(1, 1).setItem(s);
		board.getCell(0, 1).setItem(s2);
		board.getCell(2, 1).setItem(new Capital());
		board.getCell(2, 2).setItem(new Soldier(SoldierLevel.level2));
		board.getCell(1, 2).setItem(new Soldier(SoldierLevel.level1));
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

//	@Test
//	public void testMerge() {
//		district2.setPlayer(p1);
//		board.setSelectedCell(board.getCell(1, 1));
//		shop.setSelectedItem(new Soldier(p1), board.getSelectedCell().getDistrict());
//		board.placeNewItem(board.getCell(1, 2));
//		assertTrue(board.getCell(2, 2).getDistrict() == board.getCell(1, 1).getDistrict());
//	}
	
}
