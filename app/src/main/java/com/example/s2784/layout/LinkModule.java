package com.example.s2784.layout;

public class LinkModule {

    public interface MListener {
        void updateMsg(String sender, String text);
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

    public void callUpdateMsg(String sender, String text) {
        if(mListener != null) {
            mListener.updateMsg(sender,text);
        }
    }
}
