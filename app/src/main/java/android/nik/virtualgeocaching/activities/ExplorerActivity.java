package android.nik.virtualgeocaching.activities;

import android.content.Intent;
import android.nik.virtualgeocaching.model.Chest;
import android.nik.virtualgeocaching.model.Map;
import android.nik.virtualgeocaching.adapters.MapAdapter;
import android.nik.virtualgeocaching.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
        map = new Map();
        //FIREBASE
        mDatabase = FirebaseDatabase.getInstance().getReference().child("chests");

        mDatabase.addValueEventListener(new ValueEventListener() {
            List<Chest> fChestList = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            fChestList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Chest chest  = postSnapshot.getValue(Chest.class);
                    fChestList.add(chest);
                }
                map.setChestList(fChestList);
                reloadChestList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //BUTTONS
        findViewById(R.id.adder_button).setOnClickListener(this);


        final ListView chestList = (ListView)findViewById(R.id.container_list);
        this.explorerChestList = chestList;
        mapAdapter = new MapAdapter(map);
        explorerChestList.setAdapter(mapAdapter);

        mapAdapter.setButtonClickListener(new MapAdapter.ButtonClickListener() {
            @Override
            public void onButtonClicked(Chest chest) {
                Intent chestActivity = new Intent(ExplorerActivity.this, ChestEditActivity.class);
                chestActivity.putExtra("selectedChest",chest);
                startActivity(chestActivity);
            }
        });
        explorerChestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chest chest = map.getChestList().get(position);
                Intent mapa = new Intent(ExplorerActivity.this, MapActivity.class);
                mapa.putExtra("selectedChest",chest);
                mapa.putExtra("globalMap",map);
                startActivity(mapa);
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
