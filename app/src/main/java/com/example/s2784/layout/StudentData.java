package com.example.s2784.layout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_data);

        Intent intentFromLogin = getIntent();
        studentID = intentFromLogin.getStringExtra("studentID");

        img_proPic = findViewById(R.id.proPic);
        txv_studentID = findViewById(R.id.studentID);
        txv_name = findViewById(R.id.name);


        getDataAndDisplay();
    }

    private void getDataAndDisplay(){

        class GetData extends AsyncTask<String,Void,StudentInfo> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(StudentData.this, "Gain Student Data", "Please wait...", true, true);
            }
            @Override
            protected void onPostExecute(StudentInfo info) {
                super.onPostExecute(info);

                loading.dismiss();
                img_proPic.setImageBitmap(info.bpProPic);
                txv_studentID.setText( "學號 : " + info.studentID);
                txv_name.setText( "姓名: " + info.studentName);
            }

            @Override
            protected StudentInfo doInBackground(String...params) {
                String address = "http://140.116.82.52:80/phpCode/selectByStudentID.php?StudentID=" + params[0];
                String address2 = "http://140.116.82.52:80/phpCode/getPic.php?StudentID=" + params[0];

                String jsonString = null;
                Bitmap image = null;

                StudentInfo info = new StudentInfo();

                String[] result = new String[2];
                try {
                    URL url = new URL(address);
                    InputStream inputStream = url.openConnection().getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf8"));
                    StringBuilder builder = new StringBuilder();
                    String line = null;
                    while((line = bufferedReader.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    inputStream.close();
                    jsonString = builder.toString();
                    url = new URL(address2);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                result = DecodeJSON(jsonString);
                info.studentName = result[0];
                info.studentID = result[1];
                info.bpProPic = image;
                return info;
            }
        }

        GetData getdata = new GetData();
        getdata.execute(studentID);
    }

    public void onClick(View v){

        finish();
    }


    private final String[] DecodeJSON(String input) {
        String[] info = new String[2];
        try {
            JSONArray jsonArray = new JSONArray(input);
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                info[0] = jsonData.getString("Name");
                info[1] = jsonData.getString("StudentID");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }
    class StudentInfo{
        Bitmap bpProPic;
        String studentName;
        String studentID;

    }
}
