package android.nik.virtualgeocaching.activities;

import android.content.Intent;
import android.nik.virtualgeocaching.R;
import android.nik.virtualgeocaching.model.Chest;
import android.nik.virtualgeocaching.model.Map;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class CreateChestActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText chestIDField;
    private EditText radiusField;
    private SwitchCompat publicViewSwitch;
    private SwitchCompat publicEditSwitch;
    private Map map;

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
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createChestButton) {
            if (inputValidation()){
                Chest newChest = new Chest(new LatLng(47.528807, 21.624475),
                        chestIDField.getText().toString(),
                        (float)Integer.valueOf(radiusField.getText().toString()),
                        publicViewSwitch.isChecked(),
                        "tesztadventurerID",
                        publicEditSwitch.isChecked());
                map.addChest(newChest);
                Intent resultIntent = this.getIntent();
                resultIntent.putExtra("resultMap",map);
                CreateChestActivity.this.setResult(1,resultIntent);
                CreateChestActivity.this.finishActivity(100);
                return;
                //Intent explorer = new Intent(CreateChestActivity.this, ExplorerActivity.class);
               // startActivity(explorer);
            }
            else
                Toast.makeText(CreateChestActivity.this, "Every field must be filled!", Toast.LENGTH_SHORT).show();
        }
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

}
