package com.example.s2784.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MCItemsAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> mc_items;

    public MCItemsAdapter(Context context, ArrayList<String> items) {
        this.context = context;
        this.mc_items = items;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mc_items.size();
    }

    @Override
    public Object getItem(int position) {
        return mc_items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        if(rowView == null) {
            rowView = inflater.inflate(R.layout.mc_row_item, null);
        }

        String item = (String)this.getItem(position);
        TextView textView = rowView.findViewById(R.id.mc_item_tv);
        textView.setText(item);

        return rowView;
    }

    public void add_item(String item)
    {
        this.mc_items.add(item);
    }

}
