package com.example.s2784.layout;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

public class AnnocViewModel extends ViewModel
{
    private ArrayList<String> text_list = new ArrayList<>();
    private String annocBuffer = null;

    public ArrayList<String> getText_list() {
        return text_list;
    }

    public void add_annoc(String text) {
        text_list.add(text);
    }

    public void add_annoc_soon(String pk) {
        text_list.add(String.format("%s\n%s", pk, this.getAnnocBuffer()));
    }


    public String getAnnocBuffer() {
        return annocBuffer;
    }

    public void setAnnocBuffer(String annocBuffer) {
        this.annocBuffer = annocBuffer;
    }

}
