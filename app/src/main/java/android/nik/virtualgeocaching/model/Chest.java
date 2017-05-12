package android.nik.virtualgeocaching.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Zsu on 2017. 04. 19..
 */

public class Chest implements Parcelable {

    private ModelLatLng position;
    private float radius;   // Radius
    private boolean hidden;
    private boolean opentoEdit;
    //chestID must not be changed
    private String chestID;
    private String adventurerID;

    public Chest(ModelLatLng position, String chestID, float radius, boolean hidden, String adventurerID , boolean opentoEdit) {
        this.setPosition(position);
        this.chestID = chestID;
        this.setRadius(radius);
        this.setHidden(hidden);
        this.setAdventurerID(adventurerID);
        this.setOpentoEdit(opentoEdit);
    }
    public Chest(){}

    public ModelLatLng getPosition() {
        return position;
    }

    public String getChestID() {
        return chestID;
    }

    public void setPosition(ModelLatLng position) {
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

    public boolean isOpentoEdit() {
        return opentoEdit;
    }

    public void setOpentoEdit(boolean opentoEdit) {
        this.opentoEdit = opentoEdit;
    }

    public String getAdventurerID() {
        return adventurerID;
    }

    public void setAdventurerID(String adventurerID) {
        this.adventurerID = adventurerID;
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
        dest.writeByte(this.opentoEdit ? (byte) 1 : (byte) 0);
        dest.writeString(this.chestID);
        dest.writeString(this.adventurerID);
    }

    protected Chest(Parcel in) {
        this.position = in.readParcelable(LatLng.class.getClassLoader());
        this.radius = in.readFloat();
        this.hidden = in.readByte() != 0;
        this.opentoEdit = in.readByte() != 0;
        this.chestID = in.readString();
        this.adventurerID = in.readString();
    }

    public static final Creator<Chest> CREATOR = new Creator<Chest>() {
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
