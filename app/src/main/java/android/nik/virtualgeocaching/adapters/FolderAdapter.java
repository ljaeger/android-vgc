package android.nik.virtualgeocaching.adapters;

import android.nik.virtualgeocaching.R;
import android.nik.virtualgeocaching.support.StringUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Laszlo on 5/14/2017.
 */

public class FolderAdapter extends BaseAdapter {
    List<String> downloadList;

    public FolderAdapter(List<String> downloadList) {this.downloadList = downloadList;}
    public void setList(List<String> updatedList){this.downloadList = updatedList;}

    @Override
    public int getCount() {
        return downloadList.size();
    }

    @Override
    public Object getItem(int position) {
        return downloadList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String completeURL = downloadList.get(position);
        String fileName = StringUtils.getFileNameFromDownloadURL(completeURL);

        if(convertView == null){
            convertView = View.inflate(parent.getContext(), R.layout.listitem_downloadurl,null);
        }
        TextView urlText = (TextView) convertView.findViewById(R.id.urlText);
        urlText.setText(fileName);

        return convertView;
    }

}
