package com.example.s2784.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RoomInfo> friendList;
    private static LayoutInflater inflater = null;

    public ListViewAdapter(Context context, ArrayList<RoomInfo> friendList) {
        this.context = context;
        this.friendList = friendList;
        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final RoomInfo roomInfo = (RoomInfo)getItem(position);

        View rowView = inflater.inflate(R.layout.list_view_item, null);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        imageView.setImageBitmap(roomInfo.getIcon());
        name.setText(roomInfo.getStudentID());

        return rowView;
    }



}
