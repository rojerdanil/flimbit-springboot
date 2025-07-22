package com.riseup.flimbit.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class AnnouncementRequest {
    private String title;
    private String message;
    private int languageId = 0;
    private String startDatetime;
    private String validUntil;
    private String imageUrl;
    private String linkUrl;
    private String priority = "NORMAL";
    private String status = "ACTIVE";
    private boolean notifyEmail;
    private boolean notifyPush;
    private boolean notifySMS;
    private boolean notifyInApp;

    
}
