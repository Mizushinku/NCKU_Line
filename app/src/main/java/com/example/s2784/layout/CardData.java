package com.example.s2784.layout;

import android.os.Parcel;
import android.os.Parcelable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardData implements Parcelable {

    private String title;
    private String content;
    private String time;
    private String name;
    private String type;

    public CardData(){}

    public CardData(Parcel in){
        title = in.readString();
        content = in.readString();
        time = in.readString();
        name = in.readString();
        type = in.readString();
    }

    public CardData(String title,String content,String time,String name, String type){
        this.title = title;
        this.content = content;
        this.time = time;
        this.name = name;
        this.type = type;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getName() { return name; }

    public void setName(String name ) { this.name = name; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(time);
        dest.writeString(name);
        dest.writeString(type);
    }

    public static Creator<CardData> CREATOR = new Creator<CardData>() {
        @Override
        public CardData createFromParcel(Parcel source) { return new CardData(source); }

        @Override
        public CardData[] newArray(int size) { return new CardData[size]; }
    };

}

