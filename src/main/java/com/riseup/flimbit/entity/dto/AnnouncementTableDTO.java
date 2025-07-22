package com.riseup.flimbit.entity.dto;

import java.sql.Timestamp;

public interface AnnouncementTableDTO {
	
	int getId();
    String getTitle();
    String getStatus();
    Integer getLanguageId();
    String getLanguageName();
    String getStartTime();
    String getCreatedAt();
    String getValidUntil();
    String getImageUrl();
    String getLinkUrl();
    String getPriority();
    String getUpdateDate();
    String getMessage();
    boolean getIsNotifyEmail();
    boolean getIsNotifyPush();
    boolean getIsNotifySms();
    boolean getIsNotifyInApp();

    
    

}
