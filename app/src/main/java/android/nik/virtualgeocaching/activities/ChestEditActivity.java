package android.nik.virtualgeocaching.activities;

import android.nik.virtualgeocaching.R;
import android.nik.virtualgeocaching.model.Chest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ChestEditActivity extends AppCompatActivity {

    private TextView chestIDTextView;
    private TextView chestRadiusTextView;
    private TextView chestListedTextView;
    private TextView openToEditTextView;
    private Chest chest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_edit);
        Chest selectedChest = (Chest) getIntent().getParcelableExtra("selectedChest");
        this.chest = selectedChest;

        //VIEWS
        chestIDTextView = (TextView) findViewById(R.id.chestIDText);
        chestRadiusTextView = (TextView) findViewById(R.id.radiusText);
        chestListedTextView = (TextView) findViewById(R.id.listedText);
        openToEditTextView = (TextView) findViewById(R.id.opentoEditText);

        chestIDTextView.setText(chest.getChestID());
        chestRadiusTextView.setText(Float.toString(chest.getRadius()));
    }
}
