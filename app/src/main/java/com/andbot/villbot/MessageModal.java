package com.andbot.villbot;

import java.util.Locale;

public class MessageModal {

    public String message;
    public String sender;

    public int getColor_tv() {
        return color_tv;
    }

    public int color_tv;

    public String getBmg() {
        return bmg;
    }

    public String bmg;
    public MessageModal(String message, String sender, String gen, int color_tv) {
        this.message = message;
        this.sender = sender;
        if(!gen.equals("B")){
            bmg = gen.toLowerCase(Locale.ROOT) + ".json";
        }
        this.color_tv = color_tv;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
