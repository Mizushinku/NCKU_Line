package com.example.s2784.layout;

public class LinkModule {

    public interface MListener {
        void foo(String s);
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

    public void callFoo(String s) {
        if(mListener != null) {
            mListener.foo(s);
        }
    }
}
