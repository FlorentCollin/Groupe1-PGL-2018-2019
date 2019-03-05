package gui.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

public class Language {
    public static I18NBundle bundle;

    public static void init() {
        FileHandle base = Gdx.files.internal("languages/Slay_texts");
        bundle = I18NBundle.createBundle(base);
    }

    public static void setLanguage(String language) {
        FileHandle base = Gdx.files.internal("languages/Slay_texts");
        Locale loc = new Locale(language);
        bundle = I18NBundle.createBundle(base, loc);

    }
}
