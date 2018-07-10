package com.example.s2784.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by uscclab on 2018/5/21.
 * 聊天室內的list view的Adapter
 * msgList 中儲存一則則的聊天訊息
 * notifyDataSetChanged Method 實作一次只更新一行的方法(不用一次更新整個listView)
 * ... 2018/6/30
 */

public class bubble_list  extends BaseAdapter {

    private Context context;
    private ArrayList<bubble> msgList;
    private static LayoutInflater inflater = null;

    public bubble_list(Context context, ArrayList<bubble> msgList) {
        this.context = context;
        this.msgList = msgList;
        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        final bubble Bubble = (bubble)getItem(position);
        final RoomInfo roomInfo;
        TextView txt_msg = null;
        TextView name = null;
        ImageView pic = null;
        int type = Bubble.getType();
        if(type == 0){
            rowView = inflater.inflate(R.layout.bubble_chat_left, null);
            name = (TextView) rowView.findViewById(R.id.userName);
            name.setText(Bubble.getName());
            pic = (ImageView) rowView.findViewById(R.id.bubblePic);

        }
        else{
            rowView = inflater.inflate(R.layout.bubble_chat_right, null);
        }

        txt_msg = (TextView) rowView.findViewById(R.id.txt_msg);

        txt_msg.setText(Bubble.getTxtMsg());


        return rowView;
    }

    public void notifyDataSetChanged(ListView lv, int position) {
        //listView 可能很長(超過手機畫面),這裡拿到處於手機畫面中,第一以及最後一個的item的index
        int firstVisiblePosition = lv.getFirstVisiblePosition();
        int lastVisiblePosition = lv.getLastVisiblePosition();

        //要更新的行數處在手機畫面中才更新(不再畫面中的item,隨著listView捲動會自動更新)
        if(position >= firstVisiblePosition && position <= lastVisiblePosition) {
            View v = lv.getChildAt(position - firstVisiblePosition);
            if(v == null) {
                return;
            }

            //更新
            this.getView(position,v,lv);
        }
    }
}
