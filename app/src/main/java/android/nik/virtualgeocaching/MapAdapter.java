package android.nik.virtualgeocaching;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Laszlo on 4/19/2017.
 */

public class MapAdapter extends BaseAdapter {

    private Map map;

    public MapAdapter(Map map) {
        this.map = map;
    }

    @Override
    public int getCount() {
        return map.getChestList().size();
    }

    @Override
    public Object getItem(int position) {
        return map.getChestList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Chest chest = map.getChestList().get(position);

        if(convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.listitem_chest, null);

        }

        TextView chestPositionTextView = (TextView) convertView.findViewById(R.id.chestPosition);
        Button editButton = (Button) convertView.findViewById(R.id.editButton);
        Button viewButton = (Button) convertView.findViewById(R.id.viewButton);

        chestPositionTextView.setText(chest.getPosition().toString());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExplorerState.GetExplorerStateInstance().setButtonTouched(true);
            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExplorerState.GetExplorerStateInstance().setButtonTouched(true);
            }
        });

        return convertView;
    }
}
