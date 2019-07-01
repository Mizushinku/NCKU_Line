package com.example.s2784.layout;

import android.app.DialogFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


//        onAttach()：Fragment和Activity相關聯時調用。可以通過該方法獲取Activity引用，還可以通過getArguments()獲取參數。
//        onCreate()：Fragment被創建時調用。
//        onCreateView()：創建Fragment的佈局。
//        onActivityCreated()：當Activity完成onCreate()時調用。
//        onStart()：當Fragment可見時調用。
//        onResume()：當Fragment可見且可交互時調用。
//        onPause()：當Fragment不可交互但可見時調用。
//        onStop()：當Fragment不可見時調用。
//        onDestroyView()：當Fragment的UI從視圖結構中移除時調用。
//        onDestroy()：銷毀Fragment時調用。
//        onDetach()：當Fragment和Activity解除關聯時調用

public class Tab1 extends Fragment implements Tab1_CM.CtrlUnit {


    private CircleImageView userIcon_tab1;
    private TextView userName_tab1;
    private TextView userIntro;
    private TextView userPassword;
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    public static List<String> listDataHeader;
    private OnFragmentInteractionListener mListener;
    private TestViewModel testViewModel;
    private String userID;
    private boolean isTopShow = true;
    private boolean scrollflag = false;
    private int lastVisibleItemPosition = 0; // 上次滑動初始位置
    private int visiblePosition;
    private LinearLayoutManager linearLayoutManager;
    public Tab1() {
        // Required empty public constructor
    }


//目前用不到
    public static Tab1 newInstance(int index) {
        Tab1 tab1 = new Tab1();
        Bundle args = new Bundle();
        args.putInt("index",index);
        tab1.setArguments(args);
        return tab1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testViewModel = ViewModelProviders.of(getActivity()).get(TestViewModel.class);
        Tab1_CM.getInstance().setListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        initData();

        View view = inflater.inflate(R.layout.fragment_tab1,container,false);

        userIcon_tab1 = (CircleImageView) view.findViewById(R.id.userIcon_tab1);
        userName_tab1 = (TextView) view.findViewById(R.id.userName_tab1);
        userIntro = view.findViewById(R.id.intro);
        userPassword = view.findViewById(R.id.intro);

        userName_tab1.setText(testViewModel.getUserName());
        userIntro.setText(SQLiteManager.getIntro());
//        userPassword.setText(SQLiteManager.getPassword()); //for debug
//        Log.d("NAME","NAME:" + userName_tab1.getText());
        userIcon_tab1.setImageBitmap(testViewModel.getUserIcon());
        userIcon_tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent StudentData = new Intent(getActivity(), StudentData.class);
                StudentData.putExtra("name",testViewModel.getUserName());
                StudentData.putExtra("ID",testViewModel.getUserID());
                StudentData.putExtra("MeOrNot","1"); //It's me
                //StudentData.putExtra("image",testViewModel.getUserIcon());

                startActivity(StudentData);
            }
        });
        listView = (ExpandableListView)view.findViewById(R.id.lvExp);
        listAdapter = new ExpandableListAdapter(getActivity(),listDataHeader,testViewModel.getListHash());
        listView.setAdapter(listAdapter);

        for (int i = 0; i < 3; i++) {
            listView.expandGroup(i);
        }

        final AppBarLayout tab1_profile = view.findViewById(R.id.tab1_profile);

//        final CoordinatorLayout tab1_layout = view.findViewById(R.id.tab1_layout);
//        final LinearLayout  = view.findViewById(R.id.);
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {

//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    visiblePosition = listView.getFirstVisiblePosition();
//                    if (visiblePosition == 0) {
//                        tab1_profile.setExpanded(true,true);
//                    }
//                }

//                switch (scrollState) {
//                    // 螢幕不滾動時
//                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
//                        scrollflag = false;
//                        // 判斷滾到底部
//                        if (actualListView.getLastVisiblePosition() == (actualListView
//                                .getCount() - 1)) {
//
//                        }
//                         判斷滾到頂部
//                        if (actualListView.getFirstVisiblePosition() == 0) {
////
////                        }

//                        break;
//                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滾動時
//                        scrollflag = true;
//                        break;
//                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
//                        // 快速滑動時
//                        scrollflag = true;
//                        break;
//                }

//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

//                //当滑动时
//                if (scrollflag) {
//                    if (firstVisibleItem < lastVisibleItemPosition) {
//                        // 上滑跑出來
//                        tab1_layout.animate().translationY(0);
//                        .getLayoutParams().height = tab1_layout.getHeight();
//
//
//                        isTopShow = true;
//                    } else if (firstVisibleItem > lastVisibleItemPosition) {
//                        // 下滑收起來
//                        .getLayoutParams().height = tab1_layout.getHeight();
//
//                        tab1_layout.animate().translationY(-tab1_profile.getHeight()).setDuration(200);
//                        isTopShow = false;
//                    } else {
//                        return;
//                    }
//                    lastVisibleItemPosition = firstVisibleItem;//更新位置



//            }
//        });

//        listView.expandGroup(0);
//        listView.expandGroup(1);
//        //listAdapter.refresh(listView,1);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                RoomInfo roomInfo = (RoomInfo)listAdapter.getChild(groupPosition,childPosition);
                String code = roomInfo.getCode();
                Intent chat = null;
                if(!roomInfo.getType().equals("C")) {
                    chat = new Intent(getActivity(), Chatroom.class);
                } else {
                    chat = new Intent(getActivity(), Classroom.class);
                }
                chat.putExtra("code", code);
                chat.putExtra("id",testViewModel.getUserID());
                chat.putExtra("roomInfo",testViewModel.getRoomInfo(code));

                startActivity(chat);

                return false;
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    long packedPos = ((ExpandableListView) parent).getExpandableListPosition(position);
                    int groupPos = ExpandableListView.getPackedPositionGroup(packedPos);
                    int childPos = ExpandableListView.getPackedPositionChild(packedPos);


                    if (groupPos == 0) {
                        showGLCM(childPos);
                    } else if(groupPos == 1) {
                        showFLCM(childPos);
                    }
                }

                //必須回傳true,不然會和onClick搞混
                return true;
            }
        });



        return view;
    }

    private void showFLCM(int childPos) {
        DialogFragment dialogFragment = new FriendLongClickDialogFragment();
        //用Bundle傳遞參數
        Bundle bundle = new Bundle();
        bundle.putInt("childPos",childPos);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getActivity().getFragmentManager(), "FLCM");
    }

    private void showGLCM(int childPos) {
        DialogFragment dialogFragment = new GroupLongClickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("childPos",childPos);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getActivity().getFragmentManager(), "GLCM");
    }


    private void initData(){
        listDataHeader = new ArrayList<>();
        listDataHeader.add("群組");
        listDataHeader.add("好友");
        listDataHeader.add("課程");

        testViewModel.putListHash(listDataHeader.get(0),testViewModel.getGroup());
        testViewModel.putListHash(listDataHeader.get(1),testViewModel.getFriend());
        testViewModel.putListHash(listDataHeader.get(2),testViewModel.getCourse());
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void refreshExplv(int mod) {
        if(mod == 0) {
            listView.collapseGroup(0);
            listView.expandGroup(0);
        }
        else if(mod == 1) {
            listView.collapseGroup(1);
            listView.expandGroup(1);
        }
    }

}
