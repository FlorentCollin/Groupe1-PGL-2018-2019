package gui.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import gui.graphics.screens.*;
import gui.settings.InitSettings;
import gui.settings.UserSettings;
import gui.settings.UserShortcuts;

import static gui.utils.Constants.USER_SETTINGS_FILE;
import static gui.utils.Constants.USER_SHORTCUTS_FILE;

/**
 * Classe principale du jeu, c'est elle qui gère l'ensemble des menus, et la partie du joueur
 */
public class Slay extends Game {
	private MainMenuScreen mainMenuScreen;
	private SettingsMenuScreen settingsMenuScreen;
	private ShortcutsMenuScreen shortcutsMenuScreen;
    private OnlineMenuScreen onlineMenuScreen;
    private CreateRoomMenuScreen createRoomMenuScreen;

    private UserSettings userSettings;
    private UserShortcuts userShortcuts;

    @Override
	public void create () {
		mainMenuScreen = new MainMenuScreen(this);
		userSettings = InitSettings.init(USER_SETTINGS_FILE, UserSettings.class);
		userSettings.init();
		userShortcuts = InitSettings.init(USER_SHORTCUTS_FILE, UserShortcuts.class);
		this.setScreen(mainMenuScreen);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(16/255,16/255f,16/255f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}

	@Override
	public void dispose () {
    	InitSettings.dispose(USER_SETTINGS_FILE, userSettings);
    	InitSettings.dispose(USER_SHORTCUTS_FILE, userShortcuts);
    	Gdx.app.exit();
	}

	/**
	 * Méthode qui permet de changer entre les différents menus
	 * @param screen Le menu ou l'interface de jeu qui va s'afficher pour l'utilisateur
	 */
	public void changeScreen(Class<?> screen) {
	    BasicScreen nextScreen = null;
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
		} else if(screen == ShortcutsMenuScreen.class) {
		    if(shortcutsMenuScreen == null) {
		        shortcutsMenuScreen = new ShortcutsMenuScreen(this, mainMenuScreen.getStage());
            }
            nextScreen = shortcutsMenuScreen;
        } else if(screen == OnlineMenuScreen.class) {
		    if(onlineMenuScreen == null) {
		        onlineMenuScreen = new OnlineMenuScreen(this, mainMenuScreen.getStage());
            }
            nextScreen = onlineMenuScreen;
        } else if(screen == CreateRoomMenuScreen.class) {
            if(onlineMenuScreen == null) {
                createRoomMenuScreen = new CreateRoomMenuScreen(this, mainMenuScreen.getStage());
            }
            nextScreen = createRoomMenuScreen;
        }
		this.setScreen(nextScreen);

	}

	public UserSettings getUserSettings() {
		return userSettings;
	}

	public UserShortcuts getUserShortcuts() {
		return userShortcuts;
	}
}
