package com.example.s2784.layout;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import static android.app.Activity.RESULT_OK;


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

public class Tab4 extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    protected static final int REQUEST_CODE_CHOOSEPIC = 1;

    private ImageButton btn_changeIcon;
    private ImageButton btn_changeName;

    public Tab4() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Tab4 newInstance(int index) {
        Tab4 fragment = new Tab4();
        Bundle args = new Bundle();
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tab4, container, false);

        btn_changeIcon = view.findViewById(R.id.btn_changeIcon);
        btn_changeIcon.setOnClickListener(this);
        btn_changeName = view.findViewById(R.id.btn_changeName);
        btn_changeName.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        if(v == btn_changeIcon) {
            choosePic();
        } else if(v == btn_changeName) {
            changeName();
        }
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

    protected void choosePic() {
        /*
        Intent intent = new Intent(Chatroom.this, PicImageTest.class);
        startActivity(intent);
        */
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_CHOOSEPIC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            new Tab4.SendingImg(this).execute(uri);
        }
    }


    private static class SendingImg extends AsyncTask<Uri, Void, Void> {

        private WeakReference<Tab4> tab4WeakReference;

        private SendingImg(Tab4 tab4) {
            tab4WeakReference = new WeakReference<>(tab4);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*
            findViewById(R.id.progressBar_img).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar_img).bringToFront();
            */
        }

        @Override
        protected Void doInBackground(Uri... params) {
            Uri uri = params[0];
            Tabs.mqtt.changeUserIcon(uri);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if(tab4WeakReference.get() == null) {
                return;
            }
            //findViewById(R.id.progressBar_img).setVisibility(View.GONE);
        }
    }

    @TargetApi(21)
    private void changeName() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.dialog_change_name, null);

        new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText et_newName = view.findViewById(R.id.et_newName);
                        String newName = et_newName.getText().toString();
                        if(newName.length() > 16 || newName.isEmpty()) {
                            Toast.makeText(getContext(), R.string.confirm_name_format, Toast.LENGTH_LONG).show();
                        } else {
                            Tabs.mqtt.changeUserName(newName);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
