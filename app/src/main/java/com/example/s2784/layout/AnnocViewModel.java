package com.example.s2784.layout;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

public class AnnocViewModel extends ViewModel {
    private ArrayList<String> text_list = new ArrayList<>();

    public ArrayList<String> getText_list() {
        return text_list;
    }

    public void add_annoc(String text) {
        text_list.add(text);
    }
}
