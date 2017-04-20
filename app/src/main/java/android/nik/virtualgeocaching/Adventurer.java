package android.nik.virtualgeocaching;

/**
 * Created by Zsu on 2017. 04. 19..
 */

public class Adventurer {
    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    private String name;
    private String userId;  // Relation to Firebase

    public Adventurer(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }
}
