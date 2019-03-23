package gui.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import logic.board.cell.*;
import logic.item.*;

public class GsonInit {

    public static Gson initGson() {
        //TODO NEED SEE APACHE LICENSE 2.0
        //Création du Gson modifié pour pouvoir, désérializer des items selon leur classe respective
        RuntimeTypeAdapterFactory<Item> itemTypeAdapter = RuntimeTypeAdapterFactory
                .of(Item.class, "t")
                .registerSubtype(Capital.class, Capital.class.getName())
                .registerSubtype(Soldier.class, Soldier.class.getName())
                .registerSubtype(Tree.class, Tree.class.getName())
                .registerSubtype(Tomb.class, Tomb.class.getName())
                .registerSubtype(TreeOnFire.class, TreeOnFire.class.getName())
                .registerSubtype(DestroyableItem.class, DestroyableItem.class.getName());
        RuntimeTypeAdapterFactory<Cell> cellTypeAdapter = RuntimeTypeAdapterFactory
                .of(Cell.class, "t")
                .registerSubtype(WaterCell.class, WaterCell.class.getName())
                .registerSubtype(LandCell.class, LandCell.class.getName())
                .registerSubtype(DroughtCell.class, DroughtCell.class.getName())
                .registerSubtype(LavaCell.class, LavaCell.class.getName())
                .registerSubtype(BlizzardCell.class, BlizzardCell.class.getName());
        return new GsonBuilder().registerTypeAdapterFactory(itemTypeAdapter).registerTypeAdapterFactory(cellTypeAdapter).create();
    }
}
