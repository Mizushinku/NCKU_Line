package com.example.s2784.layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class AnnocListAdapter extends BaseAdapter implements View.OnClickListener {

    protected Context context;
    protected LayoutInflater inflater;

    private ArrayList<String> text_list;
    private HashMap<Integer, String> MC_map = new HashMap<>();

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
        boolean isVote = false, hasVoted = false, isMCV = false;

        if(rowView == null) {
            if(getAnnocType(text).equals(context.getResources().getString(R.string.vote)))
            {
                int pk = Integer.parseInt(text.split("\n")[0]);
                if(SQLiteManager.queryForVote(pk)) {
                    hasVoted = true;
                    rowView = inflater.inflate(R.layout.tab3_voted_list_item, null);
                    if(text.split(":::").length >= 2) {
                        isMCV = true;
                    }
                }
                else {
                    if(text.split(":::").length >= 2) {
                        rowView = inflater.inflate(R.layout.tab3_mc_vote_list_item, null);
                        isMCV = true;
                        MC_map.put(pk, text.split(":::")[1]);
                    }
                    else {
                        rowView = inflater.inflate(R.layout.tab3_vote_list_item, null);
                    }
                }
                isVote = true;
            }
            else {
                rowView = inflater.inflate(R.layout.tab3_base_list_item, null);
            }
        }
        else {
            if(getAnnocType(text).equals(context.getResources().getString(R.string.vote)))
            {
                int pk = Integer.parseInt(text.split("\n")[0]);
                if(SQLiteManager.queryForVote(pk)) {
                    hasVoted = true;
                    rowView = inflater.inflate(R.layout.tab3_voted_list_item, null);
                    if(text.split(":::").length >= 2) {
                        isMCV = true;
                    }
                }
            }
        }

        TextView textView = rowView.findViewById(R.id.item_tv);
        textView.setText(text.split(":::")[0]);

        if(isVote && !hasVoted) {
            if(!isMCV) {
                Button yes_btn = rowView.findViewById(R.id.yes_btn);
                yes_btn.setOnClickListener(this);
                yes_btn.setTag(position);
                Button no_btn = rowView.findViewById(R.id.no_btn);
                no_btn.setOnClickListener(this);
                no_btn.setTag(position);
            }
            else {
                Button MCC_btn = rowView.findViewById(R.id.MCC_btn);
                MCC_btn.setOnClickListener(this);
                MCC_btn.setTag(position);
            }
        }

        return rowView;
    }

    private String getAnnocType(String text)
    {
        return text.split("\n")[2].split(" : ")[1];
    }

    @Override
    public void onClick(View view)
    {
        int vid = view.getId();
        int position = (Integer)view.getTag();
        int pk = Integer.parseInt(((String)getItem(position)).split("\n")[0]);
        if(vid == R.id.yes_btn)
        {
            confirmVote(pk, 0);
        }
        else if(vid == R.id.no_btn)
        {
            confirmVote(pk, 1);
        }
        else if(vid == R.id.MCC_btn)
        {
            mc_vote_choose(pk);
        }
    }

    private void confirmVote(final int pk, final int selected)
    {
        new AlertDialog.Builder(context)
                .setIcon(R.drawable.vote)
                .setTitle(R.string.sure_vote)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Tabs.mqtt.voting(pk, selected);
                    }
                })
                .show();
    }

    private void mc_vote_choose(final int pk)
    {
        final String[] items = MC_map.get(pk).split("\n");
        final TextView tmp = new TextView(context);
        tmp.setText("0");
        new AlertDialog.Builder(context)
                .setIcon(R.drawable.vote)
                .setTitle(R.string.MCC)
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tmp.setText(String.valueOf(which));
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selected = Integer.parseInt(tmp.getText().toString());
                        Tabs.mqtt.voting(pk, selected);
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
