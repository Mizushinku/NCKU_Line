package com.example.s2784.layout;

public class TestMod {
    public interface TestLis {
        void foo();
    }

    private static TestMod mInstance;
    private TestLis mListener;

    private TestMod () {}

    public static TestMod getInstance() {
        if(mInstance == null) {
            mInstance = new TestMod();
        }
        return mInstance;
    }

    public void setListener(TestLis testLis) {
        mListener = testLis;
    }

    public void callFoo() {
        if(mListener != null) {
            mListener.foo();
        }
    }
}
