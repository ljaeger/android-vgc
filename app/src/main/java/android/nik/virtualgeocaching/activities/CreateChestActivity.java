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

import java.util.List;

public class CreateChestActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private EditText chestIDField;
    private EditText radiusField;
    private SwitchCompat publicViewSwitch;
    private SwitchCompat publicEditSwitch;
    private Map map;
    LocationManager locationManager;
    String provider;
    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map explorerMap = (Map) getIntent().getParcelableExtra("map");
        this.map = explorerMap;
        setContentView(R.layout.activity_create_chest);
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

        //GPS
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Criteria criteria = new Criteria();
        //provider = locationManager.getBestProvider(criteria, false);

        if (provider != null && !provider.equalsIgnoreCase("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000,1,this);

            if(location != null)
                onLocationChanged(location);
            else
                Toast.makeText(CreateChestActivity.this, "No location provider found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createChestButton) {
            if (inputValidation()){
                String chestIDString = chestIDField.getText().toString();
                if(!isDuplicateChestID(chestIDString)){
                    Chest newChest = new Chest(
                            getLocation(),
                            chestIDString,
                            (float) Integer.valueOf(radiusField.getText().toString()),
                            publicViewSwitch.isChecked(),
                            "tesztadventurerID",
                            publicEditSwitch.isChecked());
                    map.addChest(newChest);
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
        int radiusValue = Integer.valueOf(radiusField.getText().toString());
        if(chestIDText.equalsIgnoreCase("") || radiusValue == 0){
            return false;
        }
        else
            return true;
    }

    private LatLng getLocation()
    {
        return new LatLng(latitude,longitude);
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
