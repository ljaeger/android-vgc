package android.nik.virtualgeocaching.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Laszlo on 5/9/2017.
 */

public class ModelLatLng implements Parcelable {
    private Double latitude;
    private Double longitude;

    public ModelLatLng() {
    }

    public ModelLatLng(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "lat: " + latitude + " long: " + longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
    }

    protected ModelLatLng(Parcel in) {
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<ModelLatLng> CREATOR = new Parcelable.Creator<ModelLatLng>() {
        @Override
        public ModelLatLng createFromParcel(Parcel source) {
            return new ModelLatLng(source);
        }

        @Override
        public ModelLatLng[] newArray(int size) {
            return new ModelLatLng[size];
        }
    };
}
