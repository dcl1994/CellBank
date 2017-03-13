package com.example.util;

/**
 * 定义消息的实体类
 */
public class Msg {
    public static final int TYPE_RECEIVED=0;    //消息的类型：接收
    public static final int TYPE_SENT=1;        //消息的类型：发送
    private String content; //消息的内容
    private int type;       //消息的类型
    private String username; //消息发送人

    public Msg(String content, int type,String username){
        this.content=content;
        this.type=type;
        this.username=username;
    }

    public String getContent() {
        return content;
    }
    public int getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }
}
