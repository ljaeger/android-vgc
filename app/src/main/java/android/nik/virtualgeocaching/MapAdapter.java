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
    protected ButtonClickListener buttonClickListener;

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

        final Chest chest = map.getChestList().get(position);

        if(convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.listitem_chest, null);

        }

        TextView chestPositionTextView = (TextView) convertView.findViewById(R.id.chestPosition);
        TextView chestIDTextView = (TextView)convertView.findViewById(R.id.chestID);

        Button editButton = (Button) convertView.findViewById(R.id.editButton);
        Button viewButton = (Button) convertView.findViewById(R.id.viewButton);

        chestPositionTextView.setText(chest.getPosition().toString());
        chestIDTextView.setText(chest.getChestID());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonClickListener != null)
                    buttonClickListener.onButtonClicked(ExplorerButtonType.EDIT,chest);
            }
        });
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonClickListener != null)
                    buttonClickListener.onButtonClicked(ExplorerButtonType.VIEW,chest);
            }
        });

        return convertView;
    }
    public void setButtonClickListener(ButtonClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;

    }

    public interface ButtonClickListener{
        void onButtonClicked(ExplorerButtonType buttonType, Chest chest);
    }

}
