package com.example.s2784.layout;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RoomListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<RoomInfo> roomList;
    private static LayoutInflater inflater = null;

    public RoomListAdapter(Context context, ArrayList<RoomInfo> roomList) {
        this.context = context;
        this.roomList = roomList;
        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Object getItem(int position) {
        return roomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.roomlist_item,null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.room_Pic);
            viewHolder.title = (TextView) convertView.findViewById(R.id.room_Title);
            viewHolder.msg = (TextView) convertView.findViewById(R.id.room_recentMsg);
            viewHolder.date = (TextView) convertView.findViewById(R.id.room_recentDate);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final RoomInfo roomInfo = (RoomInfo) roomList.get(position);
        viewHolder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(roomInfo.getIcon_data(),0,roomInfo.getIcon_data().length));
        viewHolder.msg.setText(roomInfo.getrMsg());
        viewHolder.title.setText(roomInfo.getRoomName());
        viewHolder.date.setText(roomInfo.getrMsgDate());

        return convertView;
    }

    class ViewHolder{
        public ImageView imageView ;
        public TextView title;
        public TextView msg;
        public TextView date;
    }

    public void notifyDataSetChanged(ListView lv, int position) {
        //listView 可能很長(超過手機畫面),這裡拿到處於手機畫面中,第一以及最後一個的item的index
        int firstVisiblePosition = lv.getFirstVisiblePosition();
        int lastVisiblePosition = lv.getLastVisiblePosition();

        //要更新的行數處在手機畫面中才更新(不再畫面中的item,隨著listView捲動會自動更新)
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            View v = lv.getChildAt(position - firstVisiblePosition);
            if (v == null) {
                return;
            }

            //更新
            this.getView(position, v, lv);
        }
    }
}