package android.nik.virtualgeocaching;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Laszlo on 3/24/2017.
 */

public class ExplorerActivity extends AppCompatActivity {
    
    Map map;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        map = new Map();


        ListView chestList = (ListView)findViewById(R.id.container_list);
        MapAdapter mapAdapter = new MapAdapter(map);
        chestList.setAdapter(mapAdapter);

        mapAdapter.setButtonClickListener(new MapAdapter.ButtonClickListener() {
            @Override
            public void onButtonClicked(ExplorerButtonType buttonType, Chest chest) {
                Intent chestActivity = new Intent(ExplorerActivity.this, ChestEditActivity.class);
                startActivity(chestActivity);
                //TODO different layouts for edit and view and open a real chest
            }
        });
    }
}
