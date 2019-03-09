package gui.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import logic.item.*;

public class GsonInit {

    public static Gson initGson() {
        //TODO NEED SEE APACHE LICENSE 2.0
        //TODO THIS RUNTIMETYPEADAPTERFACTORY NEED TO BE ON A SIDE METHOD TO BE REUSE
        //Création du Gson modifié pour pouvoir, désérializer des items selon leur classe respective
        RuntimeTypeAdapterFactory<Item> itemTypeAdapter = RuntimeTypeAdapterFactory
                .of(Item.class, "type")
                .registerSubtype(Capital.class, Capital.class.getName())
                .registerSubtype(Soldier.class, Soldier.class.getName())
                .registerSubtype(Tree.class, Tree.class.getName())
                .registerSubtype(Tomb.class, Tomb.class.getName());
//        RuntimeTypeAdapterFactory<Level> levelTypeAdapter = RuntimeTypeAdapterFactory
//                .of(Level.class, "type")
//                .registerSubtype(SoldierLevel.class, SoldierLevel.class.getName());
        return new GsonBuilder().registerTypeAdapterFactory(itemTypeAdapter).create();
    }
}
