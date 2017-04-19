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

    ExplorerState explorerState;

    public boolean isButtonTouched() {
        return buttonTouched;
    }

    public void setButtonTouched(boolean buttonTouched) {
        this.buttonTouched = buttonTouched;
    }

    private boolean buttonTouched;


    Map map;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        map = new Map();
        explorerState = ExplorerState.GetExplorerStateInstance();


        ListView chestList = (ListView)findViewById(R.id.container_list);
        MapAdapter mapAdapter = new MapAdapter(map);
        chestList.setAdapter(mapAdapter);

        chestList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (explorerState.isButtonTouched()) {
                    Intent chestActivity = new Intent(ExplorerActivity.this, ChestEditActivity.class);
                    startActivity(chestActivity);

                    explorerState.setButtonTouched(false);
                }
            }
        });
    }
}
