package com.example.s2784.layout;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by uscclab on 2018/5/21.
 * 聊天室內的list view的Adapter
 * msgList 中儲存一則則的聊天訊息
 * notifyDataSetChanged Method 實作一次只更新一行的方法(不用一次更新整個listView)
 * ... 2018/6/30
 */

public class BubbleAdapter extends BaseAdapter {

    private static final int MSG_OTHERSELF = 0;
    private static final int MSG_MYSELF = 1;
    private static final int TYPE_TXT = 0;
    private static final int TYPE_IMG = 1;

    private Context context;
    private ArrayList<Bubble> msgList;
    private RoomInfo roomInfo;
    private String copyText;

    private static LayoutInflater inflater = null;
    protected LinearLayout bubble_txt_left;
    protected LinearLayout bubble_txt_left_nodate;
    protected LinearLayout bubble_txt_right;
    protected LinearLayout bubble_txt_right_nodate;
    protected LinearLayout bubble_img_left;
    protected LinearLayout bubble_img_left_nodate;
    protected LinearLayout bubble_img_right;
    protected LinearLayout bubble_img_right_nodate;


    public BubbleAdapter(Context context, ArrayList<Bubble> msgList, RoomInfo roomInfo) {
        this.context = context;
        this.msgList = msgList;
        this.roomInfo = roomInfo;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        final Bubble Bubble = (com.example.s2784.layout.Bubble) getItem(position);
        TextView name = null;
        TextView time = null;
        TextView date = null;
        ImageView pic = null;
        int type = Bubble.getType();
        int data_t = Bubble.getData_t();
        if (type == MSG_OTHERSELF) {
            if (data_t == TYPE_TXT) {
                if (position > 0 && msgList.get(position).getDate().equals(msgList.get(position - 1).getDate())) {
                    rowView = inflater.inflate(R.layout.bubble_chat_left_nodate, null);
                    bubble_txt_left_nodate = rowView.findViewById(R.id.bubble_chat_left_nodate);
                    bubble_txt_left_nodate.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            initPopWindow(v,position);
                            return true;
                        }
                    });
                } else {
                    rowView = inflater.inflate(R.layout.bubble_chat_left, null);
                    bubble_txt_left = rowView.findViewById(R.id.bubble_chat_left);
                    bubble_txt_left.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            initPopWindow(v,position);
                            return true;
                        }
                    });
                    date = (TextView) rowView.findViewById(R.id.date_left);
                    date.setText(Bubble.getDate());
                }
            } else if (data_t == TYPE_IMG) {
                if (position > 0 && msgList.get(position).getDate().equals(msgList.get(position - 1).getDate())) {
                    rowView = inflater.inflate(R.layout.bubble_chat_img_left_nodate, null);
                    bubble_img_left_nodate = rowView.findViewById(R.id.bubble_chat_img_left_nodate);
                    bubble_img_left_nodate.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            initPopWindow(v, position);
                            return true;
                        }
                    });
                } else {
                    rowView = inflater.inflate(R.layout.bubble_chat_img_left, null);
                    bubble_img_left = rowView.findViewById(R.id.bubble_chat_img_left);
                    bubble_img_left.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            initPopWindow(v, position);
                            return true;
                        }
                    });
                    date = (TextView) rowView.findViewById(R.id.date_left);
                    date.setText(Bubble.getDate());
                }
            }
            name = (TextView) rowView.findViewById(R.id.userName);
            if (Tabs.mqtt.MapAlias(Bubble.getName()) != null) {
                name.setText(Tabs.mqtt.MapAlias(Bubble.getName()));
            } else {
                name.setText(Bubble.getName());
            }
            pic = (ImageView) rowView.findViewById(R.id.bubblePic);
            if (Bubble.getPic() == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.friend);
                pic.setImageBitmap(bitmap);
            } else {
                pic.setImageBitmap(Bubble.getPic());
            }
            pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent StudentData = new Intent(context, StudentData.class);
                    if (Tabs.mqtt.MapAlias(Bubble.getName()) != null) {
                        StudentData.putExtra("name",Tabs.mqtt.MapAlias(Bubble.getName()));
                    } else {
                        StudentData.putExtra("name",Bubble.getName());
                    }
                    StudentData.putExtra("ID",Bubble.getName());
                    StudentData.putExtra("MeOrNot","0"); //my friend

                    context.startActivity(StudentData);
                }
            });
        } else {
            if (data_t == TYPE_TXT) {
                if (position > 0 && msgList.get(position).getDate().equals(msgList.get(position - 1).getDate())) {
                    rowView = inflater.inflate(R.layout.bubble_chat_right_nodate, null);
                    bubble_txt_right_nodate = rowView.findViewById(R.id.bubble_chat_right_nodate);
                    bubble_txt_right_nodate.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            initPopWindow(v,position);
                            return true;
                        }
                    });
                } else {
                    rowView = inflater.inflate(R.layout.bubble_chat_right, null);
                    bubble_txt_right = rowView.findViewById(R.id.bubble_chat_right);
                    bubble_txt_right.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            initPopWindow(v,position);
                            return true;
                        }
                    });
                    date = (TextView) rowView.findViewById(R.id.date_right);
                    date.setText(Bubble.getDate());
                }
            } else if (data_t == TYPE_IMG) {
                if (position > 0 && msgList.get(position).getDate().equals(msgList.get(position - 1).getDate())) {
                    rowView = inflater.inflate(R.layout.bubble_chat_img_right_nodate, null);
                    bubble_img_right_nodate = rowView.findViewById(R.id.bubble_chat_img_right_nodate);
                    bubble_img_right_nodate.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            initPopWindow(v, position);
                            return true;
                        }
                    });
                } else {
                    rowView = inflater.inflate(R.layout.bubble_chat_img_right, null);
                    bubble_img_right = rowView.findViewById(R.id.bubble_chat_img_right);
                    bubble_img_right.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            initPopWindow(v, position);
                            return true;
                        }
                    });
                    date = (TextView) rowView.findViewById(R.id.date_right);
                    date.setText(Bubble.getDate());
                }
            }
        }

        time = (TextView) rowView.findViewById(R.id.msg_time);
        time.setText(Bubble.getTime());
        if (data_t == TYPE_TXT) {
            TextView txt_msg = rowView.findViewById(R.id.txt_msg);
            txt_msg.setText(Bubble.getTxtMsg());
        } else if (data_t == TYPE_IMG) {
            ImageView imageView = rowView.findViewById(R.id.img_msg);
            imageView.setImageBitmap(Bubble.getImage());
        }

        return rowView;
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

    private void initPopWindow(View v, final int position) {
        View view = LayoutInflater.from(v.getContext()).inflate(R.layout.simple_popup, null, false);
        Button btn_copy = (Button) view.findViewById(R.id.copy);
        Button btn_foward = (Button) view.findViewById(R.id.foward);
        Button btn_delete = (Button) view.findViewById(R.id.delete);
        Button btn_zoomin = (Button) view.findViewById(R.id.zoomin);

        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.style.pop_anim);  //设置加载动画

        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效


        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(v, 50, 0);

        //设置popupWindow里的按钮的事件
        btn_foward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "轉寄", Toast.LENGTH_SHORT).show();
                if(context instanceof Chatroom){
                    ((Chatroom) context).forwardMSG(position);
                }
                popWindow.dismiss();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "刪除 " + msgList.get(position).getFull_time() , Toast.LENGTH_SHORT).show();
                popWindow.dismiss();
                Tabs.mqtt.DeleteMessage(roomInfo.getCode(),msgList.get(position).getFull_time());
                if(position == msgList.size() - 1){
                    if(position - 1 >= 0){
                        if(msgList.get(position-1).getData_t() == TYPE_IMG){
                            LinkModule.getInstance().callUpdateRMSG(roomInfo.getCode(),"a new image",msgList.get(position-1).getFull_time());
                        }else if(msgList.get(position-1).getData_t() == TYPE_TXT){
                            LinkModule.getInstance().callUpdateRMSG(roomInfo.getCode(),msgList.get(position-1).getTxtMsg(),msgList.get(position-1).getFull_time());
                        }
                    }else if (position - 1 < 0){
                        LinkModule.getInstance().callUpdateRMSG(roomInfo.getCode(),"No History","XXXX-XX-XX XX:XX");
                    }
                }
                msgList.remove(position);
                notifyDataSetChanged();
            }
        });

        if(msgList.get(position).getData_t() == TYPE_IMG){
            btn_copy.setText("儲存");
            btn_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindow.dismiss();
                    saveImage(msgList.get(position).getImage());
                }
            });
            btn_zoomin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindow.dismiss();
                    Toast.makeText(v.getContext(), "放大檢視", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, FullScreenImage.class);

                    //imageView.buildDrawingCache();
                    //Bitmap image= imageView.getDrawingCache();
                    //Bundle extras = new Bundle();
                    //extras.putParcelable("imagebitmap", image);
                    //intent.putExtras(extras);
                    Bitmap image = msgList.get(position).getImage();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    intent.putExtra("bitmapbytes",bytes);
                    context.startActivity(intent);
                }
            });
        }else if(msgList.get(position).getData_t() == TYPE_TXT){
            btn_copy.setText("複製");
            btn_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "複製~", Toast.LENGTH_SHORT).show();
                    ClipboardManager cbm =(ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                    copyText = msgList.get(position).getTxtMsg();
                    ClipData mClipData = ClipData.newPlainText("Label",copyText);
                    cbm.setPrimaryClip(mClipData);
                    popWindow.dismiss();
                }
            });
            btn_zoomin.setVisibility(View.INVISIBLE);
        }

        if(msgList.get(position).getType() == MSG_OTHERSELF){
            btn_delete.setVisibility(View.INVISIBLE);
        }

    }

    private void saveImage(Bitmap bitmap){
        // check external storage permission
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context,"需要存取儲存空間權限",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions((Activity)context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }else {
            SaveImageTask saveImageTask = new SaveImageTask(context);
            saveImageTask.execute(bitmap);
        }
    }

}
