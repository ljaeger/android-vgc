package android.nik.virtualgeocaching.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nik.virtualgeocaching.R;
import android.nik.virtualgeocaching.model.Chest;
import android.nik.virtualgeocaching.model.Map;
import android.nik.virtualgeocaching.model.ModelLatLng;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CreateChestActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText chestIDField;
    private EditText radiusField;
    private SwitchCompat publicViewSwitch;
    private SwitchCompat publicEditSwitch;
    private Map map;
    private DatabaseReference mDatabase;
    private List<String> downloadUrlList;

    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map explorerMap = (Map) getIntent().getParcelableExtra("map");
        this.latitude = getIntent().getDoubleExtra("explorerLatitude",0);
        this.longitude = getIntent().getDoubleExtra("explorerLongitude",0);
        this.map = explorerMap;
        setContentView(R.layout.activity_create_chest);
        //FIREBASE DATABASE
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //VIEWS
        chestIDField = (EditText) findViewById(R.id.chestIDText);
        radiusField = (EditText) findViewById(R.id.radiusText);
        radiusField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (radiusField.getText().toString().startsWith("0")) {
                    radiusField.setText(radiusField.getText().toString().substring(1));
                    Toast.makeText(CreateChestActivity.this, "Radius should not start with zero(0).", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        publicViewSwitch = (SwitchCompat) findViewById(R.id.hiddenSwitch);
        publicEditSwitch = (SwitchCompat) findViewById(R.id.opentoEditSwitch);

        //BUTTONS
        findViewById(R.id.createChestButton).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createChestButton) {
            if (inputValidation()){
                String chestIDString = chestIDField.getText().toString();
                if(!isDuplicateChestID(chestIDString)){
                    String userName = getUserName();
                    Chest newChest = new Chest(
                            //getLocation(),
                            new ModelLatLng(getLocation().latitude,getLocation().longitude),
                            chestIDString,
                            (float) Integer.valueOf(radiusField.getText().toString()),
                            publicViewSwitch.isChecked(),
                            //"tesztadventurerID",
                            userName,
                            publicEditSwitch.isChecked());
                    //chest hozzadasa a terkephez
                    map.addChest(newChest);
                    String newChestID= newChest.getChestID();
                    //chest object adatbazisba helyezese
                    mDatabase.child("chests").child(newChestID).setValue(newChest);
                    //chest lokacio mentese kulon faba
                    mDatabase.child("chestlocation").child(newChestID).setValue(newChest.getPosition());

                    Intent resultIntent = this.getIntent();
                    resultIntent.putExtra("resultMap", map);
                    CreateChestActivity.this.setResult(RESULT_OK, resultIntent);
                    CreateChestActivity.this.finishActivity(100);
                    super.onBackPressed();
                    return;
                }
                else
                    Toast.makeText(CreateChestActivity.this, chestIDField.getText().toString()+ " chest identifier already exists.", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(CreateChestActivity.this, "Every field must be filled!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUserName() {
        String userName;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser =  firebaseAuth.getCurrentUser();
        userName = firebaseUser.getDisplayName();
        if (userName == null || userName.isEmpty())
            return getString(R.string.invalid_username);
        else
            return userName;
    }

    private boolean isDuplicateChestID(String chestID) {
        List<Chest> chestList = map.getChestList();
        for (Chest chest : chestList){
            if(chest.getChestID().equalsIgnoreCase(chestID))
                return true;
        }
        return false;

    }

    private boolean inputValidation() {
        String chestIDText = chestIDField.getText().toString();
        if(radiusField.getText().toString().isEmpty())
            return false;
        int radiusValue = Integer.valueOf(radiusField.getText().toString());
        return !(chestIDText.equalsIgnoreCase("") || radiusValue == 0);
    }

    private LatLng getLocation()
    {
        return new LatLng(latitude,longitude);
    }

}
