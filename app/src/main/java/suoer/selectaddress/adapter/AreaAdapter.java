package suoer.selectaddress.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import suoer.selectaddress.R;
import suoer.selectaddress.bean.Area;

/**
 * Created by admin on 2017-04-27
 */

public class AreaAdapter extends BaseAdapter {
    private List<Area> list;
    private int index;
    public AreaAdapter(List<Area> list, int index){
        this.index = index;
        this.list = list;

    }
    public void setIndex(int index){
        this.index = index;
    }
    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }
    @Override
    public Area getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_area, parent, false);
            holder = new Holder();
            holder.textView = (TextView) convertView.findViewById(R.id.textView);
            holder.imageViewCheckMark = (ImageView) convertView.findViewById(R.id.imageViewCheckMark);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        Area item = getItem(position);
        holder.textView.setText(item.getName());

        boolean checked = index != -1 && list.get(index).getId().equals(item.getId());
        holder.textView.setEnabled(!checked);
        holder.imageViewCheckMark.setVisibility(checked ? View.VISIBLE : View.GONE);
        return convertView;
    }
    class Holder {
        TextView textView;
        ImageView imageViewCheckMark;
    }
}

