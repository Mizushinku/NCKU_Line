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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RoomListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<RoomInfo> roomList;
    private static LayoutInflater inflater = null;

    public RoomListAdapter(Context context, ArrayList<RoomInfo> roomList) {
        this.context = context;
        this.roomList = roomList;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.tab2_list_item, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.room_Pic);
            viewHolder.title = (TextView) convertView.findViewById(R.id.room_Title);
            viewHolder.msg = (TextView) convertView.findViewById(R.id.room_recentMsg);
            viewHolder.date = (TextView) convertView.findViewById(R.id.room_recentDate);
            viewHolder.badge = (TextView) convertView.findViewById(R.id.room_badge);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final RoomInfo roomInfo = (RoomInfo) roomList.get(position);
        viewHolder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(roomInfo.getIcon_data(), 0, roomInfo.getIcon_data().length));
        viewHolder.title.setText(roomInfo.getRoomName());
        if (roomInfo.getrMsg().equals("No History")) {
            viewHolder.msg.setText("");
        } else {
            viewHolder.msg.setText(roomInfo.getrMsg());
        }
        if (roomInfo.getrMsgDate().equals("XXXX-XX-XX XX:XX")) {
            viewHolder.date.setText("");
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            String now_time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calendar.getTime());
            calendar.add(Calendar.DATE, -1);
            String yest_time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calendar.getTime());
            if (now_time.substring(0, 10).equals(roomInfo.getrMsgDate().substring(0, 10))) {
                viewHolder.date.setText(roomInfo.getrMsgDate().substring(11, 16));
            } else if (yest_time.substring(0, 10).equals(roomInfo.getrMsgDate().substring(0, 10))) {
                viewHolder.date.setText("昨天");
            } else {
                String date = "";
                if (roomInfo.getrMsgDate().substring(5, 6).equals("0")) {
                    date = date.concat(roomInfo.getrMsgDate().substring(6, 7));
                } else {
                    date = date.concat(roomInfo.getrMsgDate().substring(5, 7));
                }
                date = date.concat("/");
                if (roomInfo.getrMsgDate().substring(8, 9).equals("0")) {
                    date = date.concat(roomInfo.getrMsgDate().substring(9, 10));
                } else {
                    date = date.concat(roomInfo.getrMsgDate().substring(8, 10));
                }
                viewHolder.date.setText(date);
            }
        }

        if(roomInfo.getUnReadNum() == 0){
            viewHolder.badge.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.badge.setVisibility(View.VISIBLE);
            viewHolder.badge.setText(String.valueOf(roomInfo.getUnReadNum()));
        }
//        Log.d("CODE",roomInfo.getCode() + ": " + String.valueOf(roomInfo.getUnReadRum()));


        return convertView;
    }

    class ViewHolder {
        public ImageView imageView;
        public TextView title;
        public TextView msg;
        public TextView date;
        public TextView badge;
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
