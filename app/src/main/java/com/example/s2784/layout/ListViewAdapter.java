package com.example.s2784.layout;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RoomInfo> friendList;
    private static LayoutInflater inflater = null;
    private ArrayList<String> groupMember = new ArrayList<String>();

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
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_view_item,null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.checkedTextView = (CheckedTextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final RoomInfo roomInfo = (RoomInfo)getItem(position);
        viewHolder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(roomInfo.getIcon_data(),0,roomInfo.getIcon_data().length));
        viewHolder.checkedTextView.setText(roomInfo.getStudentID());
        viewHolder.checkedTextView.setChecked(roomInfo.getChecked());
        viewHolder.checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.checkedTextView.isChecked()){
                    roomInfo.setChecked(false);
                    groupMember.remove(roomInfo.getStudentID());
                    notifyDataSetChanged();
                }else {
                    roomInfo.setChecked(true);
                    groupMember.add(roomInfo.getStudentID());
                    notifyDataSetChanged();
                }
            }
        });
        return convertView;
    }

    public ArrayList<String> getGroupMember() {
        return groupMember;
    }

    class ViewHolder{
        public ImageView imageView ;
        public CheckedTextView checkedTextView;
    }
}
