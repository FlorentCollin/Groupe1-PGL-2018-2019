package ac.umons.slay.g01.gui.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import ac.umons.slay.g01.logic.board.cell.BlizzardCell;
import ac.umons.slay.g01.logic.board.cell.Cell;
import ac.umons.slay.g01.logic.board.cell.DroughtCell;
import ac.umons.slay.g01.logic.board.cell.LandCell;
import ac.umons.slay.g01.logic.board.cell.LavaCell;
import ac.umons.slay.g01.logic.board.cell.WaterCell;
import ac.umons.slay.g01.logic.item.Capital;
import ac.umons.slay.g01.logic.item.DestroyableItem;
import ac.umons.slay.g01.logic.item.Item;
import ac.umons.slay.g01.logic.item.Soldier;
import ac.umons.slay.g01.logic.item.Tomb;
import ac.umons.slay.g01.logic.item.Tree;
import ac.umons.slay.g01.logic.item.TreeOnFire;

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
