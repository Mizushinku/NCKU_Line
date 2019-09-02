package com.example.s2784.layout;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.Locale;

public class Classroom extends Chatroom implements OnMenuItemClickListener {

    private static final int choosePic = 0;
    private static final int discuss = 1;
    private static final int changeAuth0 = 2;
    private static final int changeAuth1 = 3;
    private static final int changeAuth2 = 4;
    private static final int member = 5;
    private static final int document = 6;
    private static final int album = 7;
    private static final int schedule = 8;
    private static final int annoouncement = 9;
    private static final int assignTA = 10;

    private String A0_course_letters[] = {"選擇圖片","討論區","修改權限0","修改權限1","修改權限2","成員名單","課程文件","相簿","行事曆","公告","指派助教"};
    private String A1_course_letters[] = {"選擇圖片","討論區","修改權限0","修改權限1","修改權限2","成員名單","課程文件","相簿","行事曆","公告"};
    private String A2_course_letters[] = {"選擇圖片","討論區","修改權限0","修改權限1","修改權限2","成員名單","課程文件","相簿","行事曆",};
    private int A0_course_icons[] = {R.drawable.pic,R.drawable.discuss,R.drawable.auth0,R.drawable.auth1,R.drawable.auth2,R.drawable.group_member,R.drawable.document,R.drawable.album,R.drawable.calendar,R.drawable.announcement,R.drawable.assign_ta};
    private int A1_course_icons[] = {R.drawable.pic,R.drawable.discuss,R.drawable.auth0,R.drawable.auth1,R.drawable.auth2,R.drawable.group_member,R.drawable.document,R.drawable.album,R.drawable.calendar,R.drawable.announcement,};
    private int A2_course_icons[] = {R.drawable.pic,R.drawable.discuss,R.drawable.auth0,R.drawable.auth1,R.drawable.auth2,R.drawable.group_member,R.drawable.document,R.drawable.album,R.drawable.calendar};

    private int auth = -1;

    private ContextMenuDialogFragment contextMenuDialogFragment;
    private ArrayList<MenuObject> menuObjects;

    private View annoc_view = null;
    private String candidates = "";


    //onCreate -> startCreate -> setAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void startCreate() {
        super.startCreate();
        Tabs.mqtt.getAuth(code);
    }

    @Override
    public void setAuth(int auth) {
        this.auth = auth;
        set_gridAdapter();
        set_gridview_onItemClickListener();
        if(auth == 0) {
            Toast.makeText(this, "auth 0", Toast.LENGTH_SHORT).show();
            setContextMenu();
        }
        else if(auth == 1) {
            Toast.makeText(this, "auth 1", Toast.LENGTH_SHORT).show();
        }
        else if(auth == 2) {
            Toast.makeText(this, "auth 2", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void set_gridAdapter() {
        if(auth < 0) return;
        if(roomInfo.getType().equals("C")){
            if(auth == 0) {
                gridAdapter = new Grid_Adapter(this, A0_course_icons, A0_course_letters);
            } else if(auth == 1) {
                gridAdapter = new Grid_Adapter(this, A1_course_icons, A1_course_letters);
            } else if(auth == 2) {
                gridAdapter = new Grid_Adapter(this, A2_course_icons, A2_course_letters);
            }
        }
        gridView.setAdapter(gridAdapter);
    }

    @Override
    protected void set_gridview_onItemClickListener() {
        if(auth < 0) return;
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(roomInfo.getType().equals("C")) {
                    switch (position) {
                        case choosePic:
                            choosePic();
                            break;
                        case discuss:
                            Intent discuss = new Intent(getApplicationContext(),DiscussActivity.class);
                            discuss.putExtra("roomInfo", roomInfo);
                            startActivity(discuss);
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                        case 6:
                            break;
                        case 7:
                            break;
                        case schedule:
                            Intent calendar = new Intent(getApplicationContext(),Calendar.class);
//                            discuss.putExtra("roomInfo", roomInfo);
                            startActivity(calendar);

//                            DatePickerFragment fragment = new DatePickerFragment();
//                            fragment.show(getSupportFragmentManager(), "DatePickerFragment");
//                            TimePickerFragment timePickerFragment = new TimePickerFragment();
//                            timePickerFragment.show(getSupportFragmentManager(),"TimePickerFragment");
                            break;
                        case annoouncement:
                            if(getSupportFragmentManager().findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                                contextMenuDialogFragment.show(getSupportFragmentManager(), ContextMenuDialogFragment.TAG);
                            }
                            break;
                        default:
                            Toast.makeText(Classroom.this, "Wrong", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        });
    }

    private void setContextMenu() {
        menuObjects = new ArrayList<>();
        menuObjects.add(newMO(R.string.assignment, R.drawable.assignment));
        menuObjects.add(newMO(R.string.exam, R.drawable.exam));
        menuObjects.add(newMO(R.string.vote, R.drawable.vote));
        menuObjects.add(newMO(R.string.cancel, R.drawable.cancel));

        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize(250);
        menuParams.setMenuObjects(menuObjects);
        menuParams.setClosableOutside(true);
        menuParams.setFitsSystemWindow(true);
        menuParams.setClipToPadding(false);

        contextMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        contextMenuDialogFragment.setItemClickListener(this);
    }

    private MenuObject newMO(int titleID, int drawableID) {
        MenuObject menuObject = new MenuObject(getResources().getString(titleID));
        menuObject.setResource(drawableID);
        return menuObject;
    }

    @Override
    public  void onMenuItemClick(View v,final int p) {
        final String clicked = menuObjects.get(p).getTitle();
        final String roomName = roomInfo.getRoomName();
        if(!clicked.equals(getResources().getString(R.string.cancel))) {
            LayoutInflater inflater = LayoutInflater.from(this);

            if(clicked.equals(getResources().getString(R.string.assignment))) {
                final View view = inflater.inflate(R.layout.dialog_annoc_datetime, null);
                annoc_view = view;
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText title_et = view.findViewById(R.id.title_et);
                                String title = title_et.getText().toString();
                                TextView date_tv = view.findViewById(R.id.date_tv);
                                String date = date_tv.getText().toString();
                                TextView time_tv = view.findViewById(R.id.time_tv);
                                String time = time_tv.getText().toString();
                                String text = String.format(Locale.getDefault(),
                                         "%d\t" +
                                                "%s\t" +
                                                "From : %s\nAnnouncement : %s\nTitle : %s\nDue : %s, %s\t" +
                                                "%s %s:59",
                                        p, code, roomName, clicked, title, date, time, date, time);
                                Tabs.annocViewModel.setAnnocBuffer(text.split("\t")[2]);
                                Tabs.mqtt.pubAnnoc(text);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
            else if(clicked.equals(getResources().getString(R.string.exam)))
            {
                final View view = inflater.inflate(R.layout.dialog_annoc_dtplace, null);
                annoc_view = view;
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText title_et = view.findViewById(R.id.title_et);
                                String title = title_et.getText().toString();
                                TextView date_tv = view.findViewById(R.id.date_tv);
                                String date = date_tv.getText().toString();
                                TextView time_tv = view.findViewById(R.id.time_tv);
                                String time = time_tv.getText().toString();
                                EditText place_et = view.findViewById(R.id.place_et);
                                String place = place_et.getText().toString();
                                String text = String.format(Locale.getDefault(),
                                        "%d\t" +
                                                "%s\t" +
                                                "From : %s\nAnnouncement : %s\nTitle : %s\ndate : %s, %s\nlocation : %s\t" +
                                                "%s %s:59",
                                        p, code, roomName, clicked, title, date, time, place, date, time);
                                Tabs.annocViewModel.setAnnocBuffer(text.split("\t")[2]);
                                Tabs.mqtt.pubAnnoc(text);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
            else if(clicked.equals(getResources().getString(R.string.vote)))
            {
                final View view = inflater.inflate(R.layout.dialog_annoc_vote, null);
                annoc_view = view;

                final RadioButton MC_rb = view.findViewById(R.id.MC_rb);
                RadioGroup MC_rg = view.findViewById(R.id.vote_rg);
                MC_rg.setOnCheckedChangeListener(rg_onClickChanged);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setView(view)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText title_et = view.findViewById(R.id.title_et);
                                String title = title_et.getText().toString();
                                TextView date_tv = view.findViewById(R.id.date_tv);
                                String date = date_tv.getText().toString();
                                TextView time_tv = view.findViewById(R.id.time_tv);
                                String time = time_tv.getText().toString();
                                EditText content_et = view.findViewById(R.id.content_et);
                                String content = content_et.getText().toString();

                                String text = String.format(Locale.getDefault(),
                                        "%d\t" +
                                                "%s\t" +
                                                "From : %s\nAnnouncement : %s\nTitle : %s\ncontent : %s\nDue : %s, %s\t" +
                                                "%s %s:59",
                                        p, code, roomName, clicked, title, content, date, time, date, time);
                                String text_buffer = text.split("\t")[2];

                                if(MC_rb.isChecked()) {
                                    if(!candidates.equals("")) {
                                        text = String.format("%s\tMC\t%s", text, candidates);
                                        Tabs.annocViewModel.setAnnocBuffer(
                                                String.format("%s:::%s", text_buffer, candidates));
                                        Tabs.mqtt.pubAnnoc(text);
                                    }
                                    else {
                                        Toast.makeText(Classroom.this, R.string.MCEM3, Toast.LENGTH_LONG).show();
                                    }
                                }
                                else {
                                    text = String.format("%s\tAD", text);
                                    Tabs.annocViewModel.setAnnocBuffer(text_buffer);
                                    Tabs.mqtt.pubAnnoc(text);
                                }
                                candidates = "";
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }

            //annoc_view = null;
        }
    }

    private void changeAuth(int auth){
        this.auth  = auth;
    }

    @TargetApi(24)
    public void datePicker(View v) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH);
        final int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        final TextView date_tv = annoc_view.findViewById(R.id.date_tv);

        new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = String.format(Locale.getDefault(),"%s-%s-%s", year, month+1, dayOfMonth);
                date_tv.setText(date);
            }
        }, year, month, day).show();
    }

    public void timePicker(View v) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR);
        int min = calendar.get(java.util.Calendar.MINUTE);

        final TextView time_tv = annoc_view.findViewById(R.id.time_tv);

        new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format(Locale.getDefault(),"%02d:%02d", hourOfDay, minute);
                time_tv.setText(time);
            }
        }, hour, min, false).show();
    }

    private RadioGroup.OnCheckedChangeListener rg_onClickChanged =
            new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(checkedId == R.id.MC_rb) {
                        make_MCVote_item();
                    }
                }
            };

    private void make_MCVote_item()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.dialog_mc_items, null);

        ArrayList<String> items = new ArrayList<>();

        final ListView listView = view.findViewById(R.id.mc_items_lv);
        final MCItemsAdapter mcItemsAdapter = new MCItemsAdapter(this, items);
        listView.setAdapter(mcItemsAdapter);

        final EditText mc_item_et = view.findViewById(R.id.mc_item_et);
        ImageButton mc_add_btn = view.findViewById(R.id.mc_add_btn);
        mc_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = mc_item_et.getText().toString().replaceAll("[\n\t]", "");
                if(item.length() == 0) {
                    Toast.makeText(Classroom.this, R.string.MCEM1, Toast.LENGTH_LONG).show();
                }
                else {
                    mcItemsAdapter.add_item(item);
                    mcItemsAdapter.notifyDataSetChanged();
                    mc_item_et.setText("");
                }
            }
        });

        final EditText flag = new EditText(Classroom.this);
        final EditText tmp = new EditText(Classroom.this);
        new AlertDialog.Builder(this)
                .setView(view)
                .setTitle(R.string.MCDT)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mcItemsAdapter.getCount() < 2) {
                            Toast.makeText(Classroom.this, R.string.MCEM2, Toast.LENGTH_LONG).show();
                            flag.setText("0");
                        }
                        else {
                            String cand = (String)mcItemsAdapter.getItem(0);
                            for(int i = 1; i < mcItemsAdapter.getCount(); ++i) {
                                cand = String.format("%s\n%s", cand, (String)mcItemsAdapter.getItem(i));
                            }
                            flag.setText("1");
                            tmp.setText(cand);
                            candidates = tmp.getText().toString();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flag.setText("0");
                        dialog.cancel();
                    }
                })
                .show();
    }

}
