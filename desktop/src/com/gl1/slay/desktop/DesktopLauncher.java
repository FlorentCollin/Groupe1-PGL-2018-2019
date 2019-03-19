package com.gl1.slay.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import gui.app.Slay;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Slay";
		config.width = 1280; config.height= 720;
		new LwjglApplication(new Slay(), config);
		Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
	}
}
