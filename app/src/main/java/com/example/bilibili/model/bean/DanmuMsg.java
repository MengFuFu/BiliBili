package com.example.bilibili.model.bean;

/**
 * 一条弹幕消息： 发送者昵称 + 内容
 */
public class DanmuMsg {

    private String senderNick; //发送者昵称
    private String content; //弹幕内容

    public DanmuMsg(String senderNick, String content) {
        this.senderNick = senderNick;
        this.content = content;
    }

    public String getSenderNick() {
        return senderNick;
    }

    public void setSenderNick(String senderNick) {
        this.senderNick = senderNick;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static DanmuMsg mock(int i) {
        return new DanmuMsg("用户" + (i % 20 + 1), "这是测试弹幕 " + (i + 1));
    }
}
