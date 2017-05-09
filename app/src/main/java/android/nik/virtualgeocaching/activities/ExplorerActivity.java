package android.nik.virtualgeocaching.activities;

import android.content.Intent;
import android.nik.virtualgeocaching.model.Chest;
import android.nik.virtualgeocaching.support.ExplorerButtonType;
import android.nik.virtualgeocaching.model.Map;
import android.nik.virtualgeocaching.adapters.MapAdapter;
import android.nik.virtualgeocaching.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Laszlo on 3/24/2017.
 */

public class ExplorerActivity extends AppCompatActivity implements View.OnClickListener {

    Map map;
    ListView explorerChestList;
    MapAdapter mapAdapter;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);
        //FIREBASE
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //BUTTONS
        findViewById(R.id.adder_button).setOnClickListener(this);

        map = new Map();


        final ListView chestList = (ListView)findViewById(R.id.container_list);
        this.explorerChestList = chestList;
        mapAdapter = new MapAdapter(map);
        explorerChestList.setAdapter(mapAdapter);

        mapAdapter.setButtonClickListener(new MapAdapter.ButtonClickListener() {
            @Override
            public void onButtonClicked(ExplorerButtonType buttonType, Chest chest) {
                Intent chestActivity = new Intent(ExplorerActivity.this, ChestEditActivity.class);
                startActivity(chestActivity);
                //TODO different layouts for edit and view and open a real chest
            }
        });
        explorerChestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chest chest = map.getChestList().get(position);
                Intent map = new Intent(ExplorerActivity.this, MapActivity.class);
                map.putExtra("selectedChest",chest);
                startActivity(map);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.adder_button){
            Intent createChestActivity = new Intent(ExplorerActivity.this, CreateChestActivity.class);
            createChestActivity.putExtra("map",map);
            //startActivity(createChestActivity);
            startActivityForResult(createChestActivity, 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100)
            if(resultCode == RESULT_OK){
                Map returnedMap = (Map) data.getParcelableExtra("resultMap");
                this.map = returnedMap;

                reloadChestList();
            }
    }

    private void reloadChestList() {
        mapAdapter.setMap(map);
        mapAdapter.notifyDataSetChanged();
    }
}
