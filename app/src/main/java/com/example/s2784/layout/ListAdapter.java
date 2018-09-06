package com.example.s2784.layout;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.s2784.layout.databinding.RowItemBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anupamchugh on 07/02/16.
 */
public class ListAdapter extends BaseAdapter implements Filterable {

    List<RoomInfo> mData;
    List<RoomInfo> mStringFilterList;
    ValueFilter valueFilter;
    private LayoutInflater inflater;

    private SearchView context;
    private static List<String> listDataHeader;
    private  static HashMap<String,ArrayList<RoomInfo>> listHashMap;

    public ListAdapter(List<RoomInfo> cancel_type, SearchView context, List<String> listDataHeader, HashMap<String, ArrayList<RoomInfo>> listHashMap) {
        mData=cancel_type;
        mStringFilterList = cancel_type;
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
    }

//    public ListAdapter(List<String> cancel_type) {
//        mData=cancel_type;
//        mStringFilterList = cancel_type;
//    }

    public Object getChild(int i) {
        //return listDataHeader.get(i);
        return SearchView.arrayList.get(i);
        //return listHashMap.get(i);
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
        rowItemBinding.stringName.setText(mData.get(position).toString());


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
                List<RoomInfo> filterList = new ArrayList<>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).toString().toUpperCase()).contains(constraint.toString().toUpperCase())) {
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
            mData = (List<RoomInfo>) results.values;
            notifyDataSetChanged();
        }

    }

}
