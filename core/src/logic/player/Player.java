package logic.player;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;

import gui.utils.Constants;
import logic.item.Soldier;

public class Player {
	private String name;
	private Color color;
	protected ArrayList<Soldier> soldiers;

	private int id;
	
	public Player() {
		/*Dans le cas où le joueur ne stipule rien 
		 * ou est une ia*/
		soldiers =  new ArrayList<>();
	}
	
	public Player(String name) {
		/*Dans le cas d'un véritable joueur modifiant des paramètres*/
        super();
		this.name = name;

	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		// n'a une utilité que si il est possible
		// de changer se couleur pendant la partie
		this.color = color;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		//utilité idem que pour setColor
		this.name = name;
	}

    public int getId() {
	    return id;
    }

    public void setId(int id) {
	    this.id = id;
	    this.color = Constants.colors[id-1];
    }
}
