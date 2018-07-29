package com.example.s2784.layout;

/**
 * 用於Main和Chatroom的溝通的中間人
 * callback function循環中的第三者
 * 擔任橋樑的作用
 * 透過將Constructor設為private,以及getInstance Method,保證了該class的實例唯一
 * setListener Method 用於設定 "實作callback function的人"
 * 使用方法請參考 call_what_you_want Method
 * ... 2018/6/30
 */

public class LinkModule {

    public interface MListener {
        void updateMsg(String sender, String text, String time);
        void fetchRecord(String record);
        //void what_you_want();
    }

    private static LinkModule mInstance;
    private MListener mListener;

    private LinkModule() {}

    public static LinkModule getInstance() {
        if(mInstance == null) {
            mInstance = new LinkModule();
        }
        return mInstance;
    }

    public void setListener(MListener testLis) {
        mListener = testLis;
    }

//////////////////////////////////////////////////////

//    public void call_what_you_want() {
//        if(mListener != null) {
//            //do_something you want
//            mListener.what_you_want();
//        }
//    }

//////////////////////////////////////////////////////

    public void callUpdateMsg(String sender, String text, String time) {
        if(mListener != null) {
            mListener.updateMsg(sender,text,time);
        }
    }
    public void callFetchRecord(String record) {
        if(mListener != null) {
            mListener.fetchRecord(record);
        }
    }

}
