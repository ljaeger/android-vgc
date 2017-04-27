package android.nik.virtualgeocaching.model;

import android.nik.virtualgeocaching.model.Adventurer;
import android.nik.virtualgeocaching.model.Chest;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Zsu on 2017. 04. 19..
 */

public class Map {

    public List<Chest> getChestList() {
        return ChestList;
    }

    private List<Chest> ChestList;


    public Map() {
        ChestList = new ArrayList<Chest>();

        ChestList.add(new Chest(new LatLng(47.533353d, 19.034886d),"PeterBox01", 400, false, new Adventurer("Péter", "hallgato123"), true));
        ChestList.add(new Chest(new LatLng(47.532179d, 19.037279d),"AttilaBox03", 400, false, new Adventurer("Attila", "hallgato124"), true));
        ChestList.add(new Chest(new LatLng(47.535859d, 19.033138d),"LillaBox022", 400, false, new Adventurer("Lilla", "hallgato125"), true));
        ChestList.add(new Chest(new LatLng(47.532621d, 19.030906d), "EszterBox017", 400, false, new Adventurer("Eszter", "hallgato126"), true));
    }

    public void addChest(Chest chest){
        this.ChestList.add(chest);
    }
}
//TODO replace DEBUG MAP class
