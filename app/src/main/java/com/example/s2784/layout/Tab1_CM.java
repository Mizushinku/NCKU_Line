package com.example.s2784.layout;

public class Tab1_CM {

    public interface CtrlUnit {
        void refreshExplv(int mod);
    }

    private static Tab1_CM mInstance;
    private CtrlUnit ctrlUnit;

    private Tab1_CM() {}

    public static Tab1_CM getInstance() {
        if(mInstance == null) {
            mInstance = new Tab1_CM();
        }
        return mInstance;
    }

    public void setListener(CtrlUnit cu) {
        ctrlUnit = cu;
    }

    public void refreshExplv(int mod) {
        ctrlUnit.refreshExplv(mod);
    }

}
