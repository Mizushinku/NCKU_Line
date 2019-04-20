package com.example.s2784.layout;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.QBadgeView;


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
public class Tab2 extends Fragment {


    private OnFragmentInteractionListener mListener;
    private ListView roomListView;
    private RoomListAdapter roomListAdapter;
    private TestViewModel testViewModel;
    private String userID;
    public Tab2() {
        // Required empty public constructor
    }


    //目前用不到
    public static Tab2 newInstance(int index) {
        Tab2 tab2 = new Tab2();
        Bundle args = new Bundle();
        args.putInt("index",index);
        tab2.setArguments(args);
        return tab2;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testViewModel = ViewModelProviders.of(getActivity()).get(TestViewModel.class);
        Log.d("PAGE","tab2/size:"+testViewModel.getRoomList().size());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab2,container,false);
        roomListView = view.findViewById(R.id.room_lv);
//        roomList = testViewModel.getRoomList();
        roomListAdapter = new RoomListAdapter(getActivity(),testViewModel.getRoomList());
        roomListView.setAdapter(roomListAdapter);

        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RoomInfo roomInfo = (RoomInfo) testViewModel.getRoomList().get(position);
                String code = roomInfo.getCode();

                /*
                String thisRoomType = roomInfo.getType();

                if(thisRoomType.equals("F")) {
                    for (int i = 0; i < testViewModel.getFriend().size(); i++) {
                        if (code.equals(testViewModel.getFriend().get(i).getCode())) {
                            testViewModel.getFriend().get(i).setUnReadNum(0);
                            break;
                        }
                    }
                } else if(thisRoomType.equals("G")) {
                    for (int i = 0; i < testViewModel.getGroup().size(); i++) {
                        if (code.equals(testViewModel.getGroup().get(i).getCode())) {
                            testViewModel.getGroup().get(i).setUnReadNum(0);
                            break;
                        }
                    }
                } else if(thisRoomType.equals("C")) {
                    for (int i = 0; i < testViewModel.getCourse().size(); i++) {
                        if (code.equals(testViewModel.getCourse().get(i).getCode())) {
                            testViewModel.getCourse().get(i).setUnReadNum(0);
                            break;
                        }
                    }
                }
                */

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
            }
        });
        return view;
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

}
