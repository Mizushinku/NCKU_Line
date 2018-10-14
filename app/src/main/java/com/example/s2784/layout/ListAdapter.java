package com.example.s2784.layout;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.s2784.layout.databinding.RowItemBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anupamchugh on 07/02/16.
 */
public class ListAdapter extends BaseAdapter implements Filterable {

    ArrayList<RoomInfo> mData;
    ArrayList<RoomInfo> mStringFilterList;
    ValueFilter valueFilter;
    private LayoutInflater inflater;

    private SearchView context;
    private static List<String> listDataHeader;
    private  static HashMap<String,ArrayList<RoomInfo>> listHashMap;

    public ListAdapter(ArrayList<RoomInfo> cancel_type, SearchView context, List<String> listDataHeader, HashMap<String, ArrayList<RoomInfo>> listHashMap) {
        mData=cancel_type;
        mStringFilterList = cancel_type;
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        RowItemBinding rowItemBinding = DataBindingUtil.inflate(inflater, R.layout.row_item, parent, false);
        rowItemBinding.stringName.setText(mData.get(position).getRoomName());

        //for room image
//        final ViewHolder viewHolder;
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//            convertView = inflater.inflate(R.layout.row_item, null);
//            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.room_Pic);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        final RoomInfo roomInfo = (RoomInfo) getItem(position);
//        viewHolder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(roomInfo.getIcon_data(), 0, roomInfo.getIcon_data().length));


        return rowItemBinding.getRoot();
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<RoomInfo> filterList = new ArrayList<>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getRoomName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(mStringFilterList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mData = (ArrayList<RoomInfo>) results.values;
            notifyDataSetChanged();
        }

    }
    class ViewHolder {
        public ImageView imageView;
    }
}
