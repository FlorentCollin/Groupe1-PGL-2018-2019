package logic.player;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;

import logic.item.Soldier;

public class Player {
	private String name;
	private Color color;
	protected ArrayList<Soldier> soldiers;
	
	public Player() {
		/*Dans le cas o� le joueur ne stipule rien ou est une ia*/
		soldiers =  new ArrayList<Soldier>();
	}
	
	public Player(String name, Color color) {
		/*Dans le cas d'un v�ritable joueur modifiant des param�tres*/
		this.name = name;
		this.color = color;
		soldiers = new ArrayList<Soldier>();
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		// n'a une utilit� que si il est possible
		// de changer se couleur pendant la partie
		this.color = color;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		//utilit� idem que pour setColor
		this.name = name;
	}
}
