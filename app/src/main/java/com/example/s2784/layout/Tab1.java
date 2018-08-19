package com.example.s2784.layout;

import android.app.Activity;
import android.app.DialogFragment;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




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

public class Tab1 extends Fragment {



    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    public static List<String> listDataHeader;
    private OnFragmentInteractionListener mListener;
    private TestViewModel testViewModel;
    private String userID;
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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        initData();

        View view = inflater.inflate(R.layout.fragment_tab1,container,false);
        listView = (ExpandableListView)view.findViewById(R.id.lvExp);
        listAdapter = new ExpandableListAdapter(getActivity(),listDataHeader,testViewModel.getListHash());
        listView.setAdapter(listAdapter);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                RoomInfo roomInfo = (RoomInfo)listAdapter.getChild(groupPosition,childPosition);
                String code = roomInfo.getCode();

                Intent chat = new Intent(getActivity(),Chatroom.class);
                chat.putExtra("code", code);
                chat.putExtra("id",testViewModel.getUserID());
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


                    if (groupPos == 1) {
                        showFLCM(childPos);
                    } else if(groupPos == 0) {
                        showGLCM(childPos);
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

//        group = new ArrayList<>();
//        friend = new ArrayList<>();

        testViewModel.putListHash(listDataHeader.get(0),testViewModel.getGroup());
        testViewModel.putListHash(listDataHeader.get(1),testViewModel.getFriend());
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
