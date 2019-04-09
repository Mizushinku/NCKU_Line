package com.example.s2784.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ReplyDataAdapter extends RecyclerView.Adapter<ReplyDataAdapter.ViewHolder> {

    private Context context;
    private List<CardData> dataList;
    private RoomInfo roomInfo;

    public ReplyDataAdapter(Context context, List<CardData> dataList, RoomInfo roomInfo){
        this.context = context;
        this.dataList = dataList;
        this.roomInfo = roomInfo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_reply_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final CardData replyData = dataList.get(position);

        int height = viewHolder.status.getLineHeight();
        Bitmap bitmap_user = BitmapFactory.decodeResource(context.getResources(), R.drawable.user);
        bitmap_user = Bitmap.createScaledBitmap(bitmap_user, height, height, false);
        Bitmap bitmap_clock = BitmapFactory.decodeResource(context.getResources(), R.drawable.clock);
        bitmap_clock = Bitmap.createScaledBitmap(bitmap_clock, height, height, false);

        ImageSpan imageSpan_user = new ImageSpan(context, bitmap_user);
        ImageSpan imageSpan_clock = new ImageSpan(context, bitmap_clock);

        String name;

        if (Tabs.mqtt.MapAlias(replyData.getName()) != null) {
            name = Tabs.mqtt.MapAlias(replyData.getName());
        } else {
            name = replyData.getName();
        }

        String status = "--" + "@" + name + " @" + replyData.getTime().substring(0, 10);
        SpannableString spannableString = new SpannableString(status);
        spannableString.setSpan(imageSpan_user, 2, 3, 0);
        spannableString.setSpan(imageSpan_clock, 4 + name.length(), 5 + name.length(), 0);
        viewHolder.status.setText(spannableString);
        viewHolder.content.setText(replyData.getContent());
        viewHolder.name.setText(name);
        if (replyData.getName().equals(Tabs.userID)) {
            viewHolder.itemView.setOnCreateContextMenuListener(viewHolder);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView content,status,name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.reply_card_content);
            status = itemView.findViewById(R.id.reply_card_status);
            name = itemView.findViewById(R.id.reply_id);
//            itemView.setOnCreateContextMenuListener((View.OnCreateContextMenuListener) this);
        }

        private void removeItem(int position) {
            CardData data = dataList.get(position);
            Tabs.mqtt.deletePostReply(roomInfo.getCode(), data.getTitle(), data.getContent());
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
