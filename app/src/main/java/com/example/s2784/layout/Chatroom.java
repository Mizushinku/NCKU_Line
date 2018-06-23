package com.example.s2784.layout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Chatroom extends AppCompatActivity implements View.OnClickListener, LinkModule.MListener {

    private ImageButton btn;
    //    private TextView tv;
    private ListView lv;
    private ArrayList<bubble> Bubble = new ArrayList<>();
    private bubble_list Bubble_list;
    private TextView status;
    private EditText et;


//    private Socket client = null;
//    private PrintWriter pw;
//    private BufferedReader br;
//    private String CHAT_SERVER_IP = "140.116.82.52";
//    private ChatOperator chatOperator = null;
//    private boolean check = false;



    private String id;
    private String friend_id;
    private String code;



    //?
    private String chatName;
    private TextView txv_chatName;


//    private static ExecutorService FULL_TASK_EXECUTOR;
//    static {FULL_TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool();}


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG","Pause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG","Restart");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d("TAG","Resume");
//        check = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Main.mqtt.setProcessingCode("");
//        Log.d("TAG","Destroy");
//        if(client!=null) {
//            try {
//                client.close();
//                br.close();
//                pw.close();
//                Log.d("TAG", "CLOSE SOCKET");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        check = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        Log.d("TAG","Create");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");



        btn = findViewById(R.id.btn_send);
        lv = findViewById(R.id.lv);
        et = findViewById(R.id.et);
        status = findViewById(R.id.status);


        txv_chatName = findViewById(R.id.chatName);

        btn.setOnClickListener(this);

        LinkModule.getInstance().setListener(this);

        Main.mqtt.setProcessingCode(code);

//        tv.setMovementMethod(new ScrollingMovementMethod());

//        chatOperator = new ChatOperator();
//        chatOperator.executeOnExecutor(FULL_TASK_EXECUTOR);

    }

    @Override
    public void onClick(View v) {
        if(v == btn) {
            Main.mqtt.test();
        }
    }

    @Override
    public void foo(String s) {
        txv_chatName.setText(s);
    }



//    private class ChatOperator extends AsyncTask<Void,Void,Void>{
//        @Override
//        protected Void doInBackground(Void... voids) {
//            while (true) {
//                try {
//                    client = new Socket(CHAT_SERVER_IP, 5050);
//                    pw = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"), true);
//                    br = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
//                    return null;
//                } catch (UnknownHostException e) {
//                    Log.d("TAG", "Failed to connect server  " + CHAT_SERVER_IP);
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    Log.d("TAG", "Failed to connect server" + CHAT_SERVER_IP);
//                    e.printStackTrace();
//                }
//            }
//        }
//
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            IdSender idSender = new IdSender();
//            idSender.execute();
//
//            btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Sender messageSender = new Sender();
//                    messageSender.executeOnExecutor(FULL_TASK_EXECUTOR);
//                    Log.d("TAG","CLICK");
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
////                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
////                        Log.d("TAG","long");
////                    } else {
////                        messageSender.execute();
////                        Log.d("TAG","short");
////                    }
//                }
//            });
//            status.setText("Status:Connected");
//            Receiver receiver = new Receiver();
//            receiver.executeOnExecutor(FULL_TASK_EXECUTOR);
//
//        }
//
//    }
//
//    private class IdSender extends AsyncTask<Void,Void,Void>{
//        boolean done = false;
//        @Override
//        protected Void doInBackground(Void... voids) {
//            if(!done) {
//                pw.write("\t\t\babc" + id +"," + friend_id+ "\n");
//                pw.flush();
////                Log.d("TAG","YES");
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            done = true;
//        }
//    }
//
//    private class Receiver extends AsyncTask<Void,Void,Void>{
//        private String message;
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            while(true){
//                try {
//                    if(br.ready()){
//                        message = br.readLine();
//                        publishProgress(null);
//                    }
//                }catch (UnknownHostException e){
//                    e.printStackTrace();
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//                try{
//                    Thread.sleep(100);
//                }catch (InterruptedException ie){
//                }
//            }
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//            super.onProgressUpdate(values);
//            Bubble.add(new bubble(0,message+"\n"));
//            Bubble_list = new bubble_list(Chatroom.this,Bubble);
//            lv.setAdapter(Bubble_list);
//            lv.setSelection(Bubble_list.getCount());
////            tv.append(message + "\n");
////            Editable editable = tv.getEditableText();
////            Selection.setSelection(editable, editable.length());
//        }
//    }
//
//    private class Sender extends AsyncTask<Void,Void,Void>{
//        private String message;
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            while (true){
//                message = et.getText().toString();
//                Log.d("TAG","msg:" + message);
//                if(!message.equals("")) {
//                    pw.write(message + "\n");
//                    pw.flush();
//                }
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            et.setText("");
//            if(!message.equals("")) {
//
//                Bubble.add(new bubble(1,message+"\n"));
//                Bubble_list = new bubble_list(Chatroom.this,Bubble);
//                lv.setAdapter(Bubble_list);
//                lv.setSelection(Bubble_list.getCount());
////                tv.append(id + ": " + message + "\n");
////                Editable editable = tv.getEditableText();
////                Selection.setSelection(editable, editable.length());
//            }
//        }
//
//    }

}