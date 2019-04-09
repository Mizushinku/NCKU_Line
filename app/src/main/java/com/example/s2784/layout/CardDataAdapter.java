package com.example.s2784.layout;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardDataAdapter extends RecyclerView.Adapter<CardDataAdapter.ViewHolder> {

    private Context context;
    private List<CardData> dataList;
    private RoomInfo roomInfo;


    public CardDataAdapter(Context context, List<CardData> data, RoomInfo roomInfo) {
        this.context = context;
        this.dataList = data;
        this.roomInfo = roomInfo;
    }

    @NonNull
    @Override
    public CardDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_discuss_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final CardData cardData = dataList.get(position);
        if(Tabs.mqtt.MapAlias(cardData.getName()) != null){
            viewHolder.name.setText(Tabs.mqtt.MapAlias(cardData.getName()));
        }else {
            viewHolder.name.setText(cardData.getName());
        }
        viewHolder.title.setText(cardData.getTitle());
        viewHolder.content.setText(cardData.getContent());

        String time = cardData.getTime();
        String date = time.substring(0,4);
        date = date.concat("年");
        if(time.substring(5,6).equals("0")){
            date = date.concat(time.substring(6,7));
        }else{
            date = date.concat(time.substring(5,7));
        }
        date = date.concat("月");
        if(time.substring(8,9).equals("0")){
            date = date.concat(time.substring(9,10));
        }else{
            date = date.concat(time.substring(8,10));
        }
        date = date.concat("日");
        viewHolder.time.setText(date);

        viewHolder.poster_icon.setImageResource(R.drawable.man);
        viewHolder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context,"Click",Toast.LENGTH_LONG).show();
                Intent reply_activity = new Intent(context, ReplyActivity.class);
                reply_activity.putExtra("CardData", cardData);
                reply_activity.putExtra("RoomInfo", roomInfo);
                context.startActivity(reply_activity);
            }
        });

        if(cardData.getName().equals(Tabs.userID)){
            viewHolder.itemView.setOnCreateContextMenuListener(viewHolder);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView title, name, content, time;
        ImageView reply;
        private CircleImageView poster_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            name = itemView.findViewById(R.id.card_name);
            content = itemView.findViewById(R.id.card_content);
            time = itemView.findViewById(R.id.card_time);
            reply = itemView.findViewById(R.id.card_reply);
            poster_icon = itemView.findViewById(R.id.discuss_poster_icon);

//            itemView.setOnCreateContextMenuListener((View.OnCreateContextMenuListener) this);
        }

        private void removeItem(int position) {
            CardData data = dataList.get(position);
            Tabs.mqtt.deletePost(roomInfo.getCode(), data.getTitle());
            dataList.remove(position);
//            notifyItemRemoved(position);
            notifyDataSetChanged();
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {//CREATE MENU BY THIS METHOD
            MenuItem Edit = menu.add(Menu.NONE, 1, 1, "Edit");
            MenuItem Delete = menu.add(Menu.NONE, 2, 2, "Delete");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        //ADD AN ONMENUITEM LISTENER TO EXECUTE COMMANDS ONCLICK OF CONTEXT MENU TASK
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        //Do stuff
                        break;

                    case 2:
                        //Do stuff
                        removeItem(getAdapterPosition());
                        break;
                }
                return true;
            }
        };
    }
}