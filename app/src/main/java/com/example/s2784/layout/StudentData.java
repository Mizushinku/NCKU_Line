package com.example.s2784.layout;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class StudentData extends AppCompatActivity {

    private String studentID;
    private ImageView img_proPic;
    private TextView txv_studentID;
    private TextView txv_name;
    private TextView txv_Intro;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_data);
        //Change status color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        }
        //Change status color

        Log.d("Create","StudentData");

        img_proPic = findViewById(R.id.proPic);
        txv_studentID = findViewById(R.id.studentID);
        txv_name = findViewById(R.id.name);
        txv_Intro = findViewById(R.id.Intro);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String ID = intent.getStringExtra("ID");
        //byte[] img = intent.getByteArrayExtra("image");
        //Bitmap b = BitmapFactory.decodeByteArray(img,0,img.length);
        //img_proPic.setImageBitmap(b);
        String MeOrNot = intent.getStringExtra("MeOrNot");
        if(MeOrNot.equals("1")) //myself
        {
            img_proPic.setImageBitmap(Tabs.testViewModel.getUserIcon());
            txv_Intro.setText(SQLiteManager.getIntro());
        }
        else if(MeOrNot.equals("0")) //my friend
        {
            img_proPic.setImageBitmap(Tabs.mqtt.MapBitmap(ID));
            txv_Intro.setText(Tabs.mqtt.MapIntro(ID));
        }

        txv_studentID.setText(ID);
        txv_name.setText(name);
    }


    public void onClick(View v){

        finish();
    }

}
