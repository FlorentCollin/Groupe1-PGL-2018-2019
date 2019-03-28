package com.gl1.slay.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ac.umons.slay.g01.gui.app.Slay;

public class DesktopLauncher {
	@SuppressWarnings("static-access")
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Slay";
		config.width = 1280; config.height= 720;
		config.addIcon("skin/slay_icon.png", Files.FileType.Internal);
		new LwjglApplication(new Slay(), config);
		Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
	}
}
