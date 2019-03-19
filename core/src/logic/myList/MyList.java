package logic.myList;

import java.util.ArrayList;

import logic.board.cell.Cell;
import logic.player.Player;

public class MyList {
	private ArrayList<ArrayList<Node>> list;
	private ArrayList<Player> mapPlayers;
	
	public MyList() {
		list = new ArrayList<>();
		mapPlayers = new ArrayList<>();
	}
	
	public void put(Player player, int duration, ArrayList<Cell> modifications) {
		Node newNode = new Node(duration, modifications);
		int index = mapPlayers.indexOf(player);
		if(index > -1) {
			list.get(index).add(newNode);
		}
		else {
			mapPlayers.add(player);
			list.get(mapPlayers.size()-1).add(newNode);
		}
	}
	
	public ArrayList<Node> listOfPlayer(Player player) {
		int index = mapPlayers.indexOf(player);
		return list.get(index);
	}
	
	public int getDuraction(Node node) {
		node.decreaseDuration();
		return node.getDuration();
	}
	
	public ArrayList<Cell> getModifications(Node node){
		return node.getModifications();
	}
	
	private class Node {
		private int duration;
		private ArrayList<Cell> modifications;
		
		public Node(int duration, ArrayList<Cell> modifications) {
			this.duration = duration;
			this.modifications = modifications;
		}
		
		public int getDuration() {
			return duration;
		}
		
		public ArrayList<Cell> getModifications(){
			return modifications;
		}
		
		public void decreaseDuration() {
			duration --;
		}
	}

}
