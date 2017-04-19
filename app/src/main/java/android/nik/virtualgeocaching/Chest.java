package android.nik.virtualgeocaching;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Zsu on 2017. 04. 19..
 */

public class Chest {

    private LatLng position;
    private float radius;   // Radius
    private boolean hidden;
    private Adventurer adventurer;
    private boolean opentoEdit;

    public Chest(LatLng position, float radius, boolean hidden, Adventurer adventurer, boolean opentoEdit) {
        this.setPosition(position);
        this.setRadius(radius);
        this.setHidden(hidden);
        this.setAdventurer(adventurer);
        this.setOpentoEdit(opentoEdit);
    }

    public LatLng getPosition() {
        return position;
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
}
