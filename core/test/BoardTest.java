import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Before;
import org.junit.Test;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.District;
import ac.umons.slay.g01.logic.board.cell.Cell;
import ac.umons.slay.g01.logic.item.Capital;
import ac.umons.slay.g01.logic.item.Soldier;
import ac.umons.slay.g01.logic.item.Tomb;
import ac.umons.slay.g01.logic.item.Tree;
import ac.umons.slay.g01.logic.item.level.SoldierLevel;
import ac.umons.slay.g01.logic.player.Player;
import ac.umons.slay.g01.logic.shop.Shop;

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

	@Test
	public void testFusion() {
		Soldier soldier = new Soldier(SoldierLevel.level1);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(soldier, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		Soldier soldier2 = new Soldier(SoldierLevel.level1);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(soldier2, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		assertTrue(board.getCell(0, 0).getItem().getLevel() == SoldierLevel.level2);
	}

	@Test
	public void testNotFusion() {
		Soldier soldier = new Soldier(SoldierLevel.level2);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(soldier, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		Soldier soldier2 = new Soldier(SoldierLevel.level1);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(soldier2, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		assertSame(board.getCell(0, 0).getItem(), soldier);
	}

	@Test
	public void testMoveFusion() {
		district.addCapital(board.getCell(1,0));
		district2.addCapital(board.getCell(3,3));
		//Ajout d'un soldat en (0,0)
		Soldier firstSoldier = new Soldier(SoldierLevel.level1);
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(firstSoldier, board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 0));
		//Ajout d'un soldat en (0,1)
		board.setSelectedCell(board.getCell(1, 1));
		shop.setSelectedItem(new Soldier(SoldierLevel.level1), board.getSelectedCell().getDistrict());
		board.placeNewItem(board.getCell(0, 1));
		//Déplacement
		board.nextPlayer();
		board.nextPlayer();
		board.setSelectedCell(board.getCell(0, 1));
		board.move(board.getCell(0, 0));
		assertSame(board.getCell(0, 0).getItem().getLevel(), SoldierLevel.level2);
		assertSame(board.getCell(0, 0).getItem(), firstSoldier);
		assertNull(board.getCell(0, 1).getItem());
	}

	@Test
	public void testPlaceNewItemOnEnemyTerritory() {
		district.addCapital(board.getCell(0,0));
		district2.addCapital(board.getCell(3,3));
		district2.addCell(board.getCell(3,4));
		board.getCell(3,4).setDistrict(district2);
		//Ajout de soldats ennemi
		board.getCell(2,1).setDistrict(district2);
		board.getCell(2,2).setDistrict(district2);
		board.getCell(2,3).setDistrict(district2);

		//Test de placer un soldat de niveau 1 sur une cellule ennemi contenant un soldat de niveau 1
		board.getCell(2,1).setItem(new Soldier(SoldierLevel.level1));
		board.setSelectedCell(board.getCell(0,0));
		board.setShopItem(new Soldier(SoldierLevel.level1));
		board.play(board.getCell(2,1));
		assertSame(board.getCell(2,1).getDistrict(), district);

		//Test de placer un soldat de niveau 1 sur une cellule ennemi contenant un soldat de niveau 2
		board.setSelectedCell(board.getCell(0,0));
		board.getCell(2,2).setItem(new Soldier(SoldierLevel.level2));
		board.setShopItem(new Soldier(SoldierLevel.level1));
		board.play(board.getCell(2,2));
		assertSame(board.getCell(2,2).getDistrict(), district2);

		//Test de placer un soldat de niveau 2 sur une cellule ennemi contenant un soldat de niveau 2
		board.setSelectedCell(board.getCell(0,0));
		board.setShopItem(new Soldier(SoldierLevel.level2));
		board.play(board.getCell(2,2));
		assertSame(board.getCell(2,2).getDistrict(), district);

		//Test de placer un soldat de niveau 1 sur une cellule ennemi vide
		board.setSelectedCell(board.getCell(0,0));
		board.setShopItem(new Soldier(SoldierLevel.level1));
		board.play(board.getCell(2,3));
		assertSame(board.getCell(2,3).getDistrict(), district);

		//Test de placer un soldat de niveau 1 sur une capitale ennemi et test si la capitale à été recrée
		board.setSelectedCell(board.getCell(0,0));
		board.setShopItem(new Soldier(SoldierLevel.level1));
		board.play(board.getCell(3,3));
		assertSame(board.getCell(3,3).getDistrict(), district);
		assertNotNull(district2.getCapital());
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
		district.addCapital(board.getCell(0,0));
		district2.addCapital(board.getCell(3,3));
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
	public void testMoveOnEnemyTerritoryEmptyOrWithSameLevelSoldier() {
		district.addCapital(board.getCell(0, 0));
		board.getCell(1, 2).setDistrict(board.getCell(2, 2).getDistrict());
		board.getCell(2, 1).setDistrict(board.getCell(2, 2).getDistrict());
		board.getCell(2, 2).getDistrict().addCell(board.getCell(1, 2));
		board.getCell(2, 2).getDistrict().addCell(board.getCell(2, 1));
		Soldier s = new Soldier(SoldierLevel.level1);
		Soldier s2 = new Soldier(SoldierLevel.level1);
		//Player 1
		board.getCell(1, 1).setItem(s);
		board.getCell(0, 1).setItem(s2);
		//Player 2
		board.getCell(2, 1).getDistrict().addCapital(board.getCell(2, 1));
		board.getCell(1, 2).setItem(new Soldier(SoldierLevel.level1));

		board.setSelectedCell(board.getCell(1, 1));
		board.move(board.getCell(2, 2));
		assertSame(s, board.getCell(2, 2).getItem());
		assertSame(board.getCell(2, 2).getDistrict(), board.getCell(1, 1).getDistrict());
		board.setSelectedCell(board.getCell(0, 1));
		board.move(board.getCell(1, 2));
		assertSame(board.getCell(1, 2).getDistrict(), board.getCell(1, 1).getDistrict());
		assertSame(board.getCell(1, 2).getItem(), s2);
	}

	@Test
	public void testMoveOnEnemyTerritoryEmptyWithHigherSoldier() {
		district.addCapital(board.getCell(0, 0));
		district2.addCapital(board.getCell(4, 4));
		board.getCell(2, 3).setDistrict(board.getCell(0, 0).getDistrict());
		Soldier s = new Soldier(SoldierLevel.level1);
		Soldier s2 = new Soldier(SoldierLevel.level1);
		//Player 1
		board.getCell(1, 1).setItem(s);
		board.getCell(1, 2).setItem(s2);
		//Player 2
		Soldier s3 = new Soldier(SoldierLevel.level2);
		board.getCell(2, 2).setItem(s3);
		board.setSelectedCell(board.getCell(1, 1));
		board.move(board.getCell(2, 2));
		assertSame(s, board.getCell(1, 1).getItem());
		assertSame(s3, board.getCell(2, 2).getItem());

		//Test qui vérifie qu'on ne peut pas placer un soldat de niveau 1 à côté d'un soldat ennemi de niveau 2
		board.setSelectedCell(board.getCell(1, 2));
		board.move(board.getCell(2, 3));
		assertSame(s2, board.getCell(1, 2).getItem());
		assertSame(s3, board.getCell(2, 2).getItem());
	}

	/**
	 * Test qui vérifie que deux districts d'un même se fusionnent s'ils se touchent
	 */
	@Test
	public void testMergeDistrict() {
		int totalGold = district.getGold() + district2.getGold();
		//On retire deux cellules qui appartiennent au joueur 2 pour montrer que c'est bien le plus petit district qui est supprimé
		board.getCell(2,2).setDistrict(null);
		board.getCell(3,3).setDistrict(null);
		district2.setPlayer(board.getPlayers().get(0));
		district.addCapital(board.getCell(0,0));
		district2.addCapital(board.getCell(3,3));
		assertEquals(2, board.getDistricts().size());
		board.getCell(1,1).setItem(new Soldier(SoldierLevel.level1));
		board.setSelectedCell(board.getCell(1,1));
		board.move(board.getCell(2,2));
		assertEquals(1, board.getDistricts().size());
		assertEquals(totalGold, board.getDistricts().get(0).getGold());
		int totalCapital = 0;
		for(Cell cell : board.getDistricts().get(0).getCells()) {
			if(cell.getItem() != null && cell.getItem() instanceof Capital)
				totalCapital++;
		}
		//Vérification que les cellules du deuxième territoire appartiennent maintenant au plus grand territoire
		assertEquals(district, board.getCell(2,3).getDistrict());
		assertEquals(district, board.getCell(3,2).getDistrict());
		assertEquals(1, totalCapital);
		//Montre que c'est bien le plus grand district qui est conservé
		assertTrue(board.getCell(0,0).getItem() instanceof Capital);
	}

	/**
	 * Test qui vérifie qu'un territoire est détruit si il ne lui reste plus qu'une seule cellule
	 *
	 */
	@Test
	public void testDestructionOfDistrict() {
		district.addCapital(board.getCell(0,0));
		board.getCell(2,0).setItem(new Soldier(SoldierLevel.level1));
		board.getCell(2,2).setItem(new Soldier(SoldierLevel.level1));
		board.getCell(2,0).setDistrict(board.getCell(2,2).getDistrict());
		board.nextPlayer();
		board.setSelectedCell(board.getCell(2,0));
		board.move(board.getCell(1,0));
		board.setSelectedCell(board.getCell(2,2));
		board.move(board.getCell(1,1));
		board.nextPlayer();
		board.nextPlayer();
		board.setSelectedCell(board.getCell(1,1));
		board.move(board.getCell(0,1));
		assertEquals(1, board.getDistricts().size());
	}

	/**
	 * Test qui vérifie qu'un district est séparé si celui-ci est coupé en deux
	 */
	@Test
	public void testSplit() {
		//Initialisation du board
		district2.setPlayer(district.getPlayer());
		board.checkMerge(board.getCell(2,2)); //On veut que le joueur 1 ne possède qu'un seul district pour tester le split
		//Création d'un troisième district qui va venir couper en deux le district du joueur 1
		District district3 = new District(p2);
		board.addDistrict(district3);
		district3.addCell(board.getCell(1,2));
		district3.addCell(board.getCell(1,3));
		district3.addCapital(board.getCell(1,3));
		board.nextPlayer();
		district3.setGold(10000);
		board.getCell(1,2).setItem(new Soldier(SoldierLevel.level1));
		board.setSelectedCell(board.getCell(1,2));
		board.move(board.getCell(2,2));
		//On vérifie que le district du joueur un s'est bien divisé en deux
		assertEquals(3, board.getDistricts().size());

		//Vérification que l'ensemble des golds vaut toujours 20000 pour le joueur 1
		assertEquals(20000, board.getDistricts().get(0).getGold() + board.getDistricts().get(1).getGold() + board.getDistricts().get(2).getGold() - 10000);
		for(District d : board.getDistricts()) {
			assertNotNull(d.getCapital());
		}
	}

	@Test
	public void testGenerateGold() {
		district.addCapital(board.getCell(0,0));
		district2.addCapital(board.getCell(1,1));
		board.getCell(1,1).setItem(new Tree());
		board.nextPlayer();

		int numberOfTree = 0;
		for(Cell cell : district2.getCells()) {
			if(cell.getItem() != null && cell.getItem() instanceof Tree)
				numberOfTree++;
		}
		//Test de la génération de gold d'un district avec uniquement des cellules vide et une capitale
		assertEquals(10000 + district2.size() - numberOfTree, district2.getGold());
		board.nextPlayer();

		numberOfTree = 0;
		for(Cell cell : district.getCells()) {
			if(cell.getItem() != null && cell.getItem() instanceof Tree)
				numberOfTree++;
		}
		//Test de la génération de gold d'un district avec un arbre sur une des cellules et une capitale
		assertEquals(10000 + district.size() - numberOfTree, district.getGold());
		district2.setGold(10000);
		board.getCell(2,2).setItem(new Soldier(SoldierLevel.level1));
		board.nextPlayer();
		//Test de la génération de gold d'un district avec un soldat de niveau 1 sur le district et une capitale
		numberOfTree = 0;
		for(Cell cell : district2.getCells()) {
			if(cell.getItem() != null && cell.getItem() instanceof Tree)
				numberOfTree++;
		}
		assertEquals(10000 + district2.size() - numberOfTree - SoldierLevel.level1.getSalary(), district2.getGold());
	}

	@Test
	public void testSoldierDiedWhenCapitalCanNotPaidSalary() {
		district.setGold(0);
		district.addCapital(board.getCell(0,0));
		board.getCell(0,1).setItem(new Soldier(SoldierLevel.level4));
		board.nextPlayer();
		board.nextPlayer();
		assertTrue(board.getCell(0,1).getItem() instanceof Tomb);
	}
}
