package android.nik.virtualgeocaching;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Zsu on 2017. 04. 19..
 */

public class Chest implements Parcelable {

    private LatLng position;
    private float radius;   // Radius
    private boolean hidden;
    private Adventurer adventurer;
    private boolean opentoEdit;
    //chestID must not be changed
    private final String chestID;

    public Chest(LatLng position, String chestID, float radius, boolean hidden, Adventurer adventurer, boolean opentoEdit) {
        this.setPosition(position);
        this.chestID = chestID;
        this.setRadius(radius);
        this.setHidden(hidden);
        this.setAdventurer(adventurer);
        this.setOpentoEdit(opentoEdit);
    }

    public LatLng getPosition() {
        return position;
    }

    public String getChestID() {
        return chestID;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Adventurer getAdventurer() {
        return adventurer;
    }

    public void setAdventurer(Adventurer adventurer) {
        this.adventurer = adventurer;
    }

    public boolean isOpentoEdit() {
        return opentoEdit;
    }

    public void setOpentoEdit(boolean opentoEdit) {
        this.opentoEdit = opentoEdit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.position, flags);
        dest.writeFloat(this.radius);
        dest.writeByte(this.hidden ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.adventurer, flags);
        dest.writeByte(this.opentoEdit ? (byte) 1 : (byte) 0);
        dest.writeString(this.chestID);
    }

    protected Chest(Parcel in) {
        this.position = in.readParcelable(LatLng.class.getClassLoader());
        this.radius = in.readFloat();
        this.hidden = in.readByte() != 0;
        this.adventurer = in.readParcelable(Adventurer.class.getClassLoader());
        this.opentoEdit = in.readByte() != 0;
        this.chestID = in.readString();
    }

    public static final Parcelable.Creator<Chest> CREATOR = new Parcelable.Creator<Chest>() {
        @Override
        public Chest createFromParcel(Parcel source) {
            return new Chest(source);
        }

        @Override
        public Chest[] newArray(int size) {
            return new Chest[size];
        }
    };
}
