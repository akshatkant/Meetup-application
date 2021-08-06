package com.example.meetup.Utills;

public class Chat {
    private String sms,status,userId;

    public Chat() {
    }

    public Chat(String sms, String status, String userId) {
        this.sms = sms;
        this.status = status;
        this.userId = userId;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
