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
    private List<ReplyData> dataList;

    public ReplyDataAdapter(Context context, List<ReplyData> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_reply_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final ReplyData replyData = dataList.get(position);

        int height = viewHolder.status.getLineHeight();
        Bitmap bitmap_user = BitmapFactory.decodeResource(context.getResources(),R.drawable.user);
        bitmap_user = Bitmap.createScaledBitmap(bitmap_user,height,height,false);
        Bitmap bitmap_clock = BitmapFactory.decodeResource(context.getResources(),R.drawable.clock);
        bitmap_clock = Bitmap.createScaledBitmap(bitmap_clock,height,height,false);

        ImageSpan imageSpan_user = new ImageSpan(context,bitmap_user);
        ImageSpan imageSpan_clock = new ImageSpan(context,bitmap_clock);

        String status = "--" + "@" + replyData.getName() + " @" + replyData.getTime();
        SpannableString spannableString = new SpannableString(status);
        spannableString.setSpan(imageSpan_user,2,3,0);
        spannableString.setSpan(imageSpan_clock,4+replyData.getName().length(),5+replyData.getName().length(),0);
        viewHolder.status.setText(spannableString);
        viewHolder.content.setText(replyData.getContent());

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView content,status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.reply_card_content);
            status = itemView.findViewById(R.id.reply_card_status);

            itemView.setOnCreateContextMenuListener((View.OnCreateContextMenuListener) this);
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

                        break;
                }
                return true;
            }
        };
    }
}
