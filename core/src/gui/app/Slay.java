package gui.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import gui.graphics.screens.MainMenuScreen;
import gui.graphics.screens.MenuScreen;
import gui.graphics.screens.SettingsMenuScreen;

/**
 * Classe principale du jeu, c'est elle qui gère l'ensemble des menus, et la partie du joueur
 */
public class Slay extends Game {
	private MainMenuScreen mainMenuScreen;
	private SettingsMenuScreen settingsMenuScreen;

	@Override
	public void create () {
		mainMenuScreen = new MainMenuScreen(this);
		this.setScreen(mainMenuScreen);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(16/255,16/255f,16/255f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.getScreen().render(Gdx.graphics.getDeltaTime());
//		super.render();
	}

	@Override
	public void dispose () {
			Gdx.app.exit();
			System.exit(0);
	}

	/**
	 * Méthode qui permet de changer entre les différents menus
	 * @param screen Le menu ou l'interface de jeu qui va s'afficher pour l'utilisateur
	 */
	public void changeScreen(Class<?> screen) {
	    MenuScreen nextScreen = null;
		if(screen == MainMenuScreen.class) {
			if(mainMenuScreen == null) {
				mainMenuScreen = new MainMenuScreen(this);
			}
			nextScreen = mainMenuScreen;
		} else if(screen == SettingsMenuScreen.class) {
			if(settingsMenuScreen == null) {
				settingsMenuScreen = new SettingsMenuScreen(this, mainMenuScreen.getStage());
			}
			nextScreen = settingsMenuScreen;
		}
		this.setScreen(nextScreen);

	}
}
