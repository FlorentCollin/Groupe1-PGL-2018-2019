import java.util.concurrent.CopyOnWriteArrayList;

import logic.board.Board;
import logic.board.District;
import logic.item.Capital;
import logic.item.Soldier;
import logic.item.Tree;
import logic.item.level.SoldierLevel;
import logic.player.Player;
import logic.player.ai.AI;
import logic.player.ai.strategy.Strategy;
import logic.shop.Shop;

public class InitStrategy {
	private AI ai;
	private Player enemy;
	private District district, enemyDistrict;
	private Board board;
	
	public InitStrategy(Strategy strategy) {
		Player player = new Player();
		enemy = new Player();
		CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>();
		players.add(player);
		players.add(enemy);
		district = new District(player);
		board = new Board(5, 5, players, new Shop());
		board.setProbTrees(0);
		board.addDistrict(district);
		for(int i=0; i<3; i++) {
			for(int j=0; j<5; j++) {
				if(!((i == 1 && j == 3) || (i == 2 && (j == 3 || j== 4)))) {
					board.getCell(i, j).setDistrict(district);
					district.addCell(board.getCell(i, j));
				}
			}
		}
		player.setId(1);
		enemy.setId(2);
		board.changeToAI(0, strategy);
		ai = (AI) board.getPlayers().get(0);
		board.getCell(0, 0).setItem(new Capital());
		district.addCapital(board.getCell(0, 0));
		board.getCell(2, 1).setItem(new Tree());
		board.getCell(1, 2).setItem(new Soldier(SoldierLevel.level1));
		board.getCell(2, 2).setItem(new Soldier(SoldierLevel.level1));
		board.getCell(1, 1).setItem(new Soldier(SoldierLevel.level1));
	}
	
	public void initEnemy() {
		enemyDistrict = new District(enemy);
		enemyDistrict.setGold(50);
		board.addDistrict(enemyDistrict);
		enemyDistrict.addCell(board.getCell(1, 3));
		enemyDistrict.addCell(board.getCell(2, 3));
		enemyDistrict.addCell(board.getCell(2, 4));
		enemyDistrict.addCell(board.getCell(3, 3));
		board.getCell(1, 3).setDistrict(enemyDistrict);
		board.getCell(2, 3).setDistrict(enemyDistrict);
		board.getCell(2, 4).setDistrict(enemyDistrict);
		board.getCell(3, 3).setDistrict(enemyDistrict);
		board.getCell(2, 4).setItem(new Capital());
		enemyDistrict.addCapital(board.getCell(2, 4));
		board.getCell(1, 3).setItem(new Soldier(SoldierLevel.level1));
		board.getCell(3, 3).setItem(new Soldier(SoldierLevel.level2));
	}
	
	public void refresh() {
		for(int i = 0; i<board.getColumns(); i++) {
			for(int j = 0; j<board.getRows(); j++) {
				board.getCell(i, j).removeItem();
				if(board.getCell(i, j).getDistrict() != null) {
					board.getCell(i, j).getDistrict().removeCell(board.getCell(i, j));
				}
				board.getCell(i, j).removeDistrict();
			}
		}
		enemyDistrict.addCell(board.getCell(1, 3));
		enemyDistrict.addCell(board.getCell(2, 3));
		enemyDistrict.addCell(board.getCell(2, 4));
		enemyDistrict.addCell(board.getCell(3, 3));
		board.getCell(1, 3).setDistrict(enemyDistrict);
		board.getCell(2, 3).setDistrict(enemyDistrict);
		board.getCell(2, 4).setDistrict(enemyDistrict);
		board.getCell(3, 3).setDistrict(enemyDistrict);
		board.getCell(2, 4).setItem(new Capital());
		enemyDistrict.addCapital(board.getCell(2, 4));
		board.getCell(1, 3).setItem(new Soldier(SoldierLevel.level1));
		board.getCell(3, 3).setItem(new Soldier(SoldierLevel.level2));
		
		for(int i=0; i<3; i++) {
			for(int j=0; j<5; j++) {
				if(!((i == 1 && j == 3) || (i == 2 && (j == 3 || j== 4)))) {
					board.getCell(i, j).setDistrict(district);
					district.addCell(board.getCell(i, j));
				}
			}
		}
		
		
		board.getCell(0, 0).setItem(new Capital());
		district.addCapital(board.getCell(0, 0));
		board.getCell(2, 1).setItem(new Tree());
		board.getCell(1, 2).setItem(new Soldier(SoldierLevel.level1));
		board.getCell(2, 2).setItem(new Soldier(SoldierLevel.level1));
		board.getCell(1, 1).setItem(new Soldier(SoldierLevel.level1));
	}
	
	public AI getAI() {
		return ai;
	}
	
	public District getDistrict() {
		return district;
	}
	
	public District getEnemyDistrict() {
		return enemyDistrict;
	}
	
	public Player getEnemy() {
		return enemy;
	}
	
	public Board getBoard() {
		return board;
	}
	

}
