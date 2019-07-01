package com.example.s2784.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class AnnocListAdapter extends BaseAdapter {

    protected Context context;
    protected LayoutInflater inflater;

    private ArrayList<String> text_list;

    public AnnocListAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.text_list = list;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return text_list.size();
    }

    @Override
    public Object getItem(int position) {
        return text_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        String text = (String)getItem(position);

        if(rowView == null) {
            if(getAnnocType(text).equals(context.getResources().getString(R.string.vote)))
            {
                rowView = inflater.inflate(R.layout.tab3_vote_list_item,  null);
            }
            else {
                rowView = inflater.inflate(R.layout.tab3_base_list_item, null);
            }
        }

        TextView textView = rowView.findViewById(R.id.item_tv);
        textView.setText(text);

        return rowView;
    }

    private String getAnnocType(String text)
    {
        return text.split("\n")[1].split(" : ")[1];
    }

}
