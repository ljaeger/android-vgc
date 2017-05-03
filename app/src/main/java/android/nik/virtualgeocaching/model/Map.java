package android.nik.virtualgeocaching.model;

import android.nik.virtualgeocaching.model.Adventurer;
import android.nik.virtualgeocaching.model.Chest;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Zsu on 2017. 04. 19..
 */

public class Map implements Parcelable {

    public List<Chest> getChestList() {
        return ChestList;
    }

    private List<Chest> ChestList;


    public Map() {
        ChestList = new ArrayList<Chest>();

        ChestList.add(new Chest(new LatLng(47.533353d, 19.034886d),"PeterBox01", 400, false, "Péter", true));
        ChestList.add(new Chest(new LatLng(47.532179d, 19.037279d),"AttilaBox03", 400, false, "Attila", true));
        ChestList.add(new Chest(new LatLng(47.535859d, 19.033138d),"LillaBox022", 400, false, "Lilla", true));
        ChestList.add(new Chest(new LatLng(47.532621d, 19.030906d), "EszterBox017", 400, false, "Eszter", true));
    }

    public void addChest(Chest chest){
        this.ChestList.add(chest);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.ChestList);
    }

    protected Map(Parcel in) {
        this.ChestList = in.createTypedArrayList(Chest.CREATOR);
    }

    public static final Parcelable.Creator<Map> CREATOR = new Parcelable.Creator<Map>() {
        @Override
        public Map createFromParcel(Parcel source) {
            return new Map(source);
        }

        @Override
        public Map[] newArray(int size) {
            return new Map[size];
        }
    };
}
//TODO replace DEBUG MAP class
