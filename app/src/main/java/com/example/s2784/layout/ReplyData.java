package com.example.s2784.layout;

public class ReplyData {
    private String name;
    private String time;
    private String content;

    public ReplyData(){}

    public ReplyData(String name, String time, String content){
        this.name = name;
        this.time = time;
        this.content = content;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }
}
