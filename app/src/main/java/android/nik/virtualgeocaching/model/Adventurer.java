package android.nik.virtualgeocaching.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zsu on 2017. 04. 19..
 */

public class Adventurer implements Parcelable {
    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    private String name;
    private final String userId;  // Relation to Firebase

    public Adventurer(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.userId);
    }

    protected Adventurer(Parcel in) {
        this.name = in.readString();
        this.userId = in.readString();
    }

    public static final Parcelable.Creator<Adventurer> CREATOR = new Parcelable.Creator<Adventurer>() {
        @Override
        public Adventurer createFromParcel(Parcel source) {
            return new Adventurer(source);
        }

        @Override
        public Adventurer[] newArray(int size) {
            return new Adventurer[size];
        }
    };
}
