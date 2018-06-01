package com.example.s2784.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by uscclab on 2018/5/21.
 */

public class bubble_list  extends BaseAdapter {

    private Context context;
    private ArrayList<bubble> friendList;
    private static LayoutInflater inflater = null;

    public bubble_list(Context context, ArrayList<bubble> friendList) {
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
        final bubble Bubble = (bubble)getItem(position);

        View rowView = null;
        TextView txt_msg = null;
        int type = Bubble.getType();
        if(type == 0){
            rowView = inflater.inflate(R.layout.bubble_chat_left, null);
        }
        else{
            rowView = inflater.inflate(R.layout.bubble_chat_right, null);
        }

        txt_msg = (TextView) rowView.findViewById(R.id.txt_msg);
        txt_msg.setText(Bubble.getTxtMsg());

        return rowView;
    }
}
