package com.riseup.flimbit.entity.dto;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;

public interface UserWithStatusDTO {

	int getId();
    String getPhoneNumber();
    String getDeviceId();
    String getPanId();
    String getFirstName();
    String getLastName();
    String getAccessKey();
    String getEmail();
    Timestamp getCreatedDate();
    Timestamp getUpdatedDate();
    Timestamp getLastLogin();
    String getStatus();
    Integer getLanguage();

    Boolean getIsPhoneVerified();
    Boolean getIsEmailVerified();
    Boolean getIsPanVerified();
    Boolean getIsNamesVerified();
    Boolean getIsLanguageVerified();

    @Value("#{target.languageName}")
    String getLanguageName();
    
    BigDecimal  getWalletBalance();
    String  getWalletUpdate();
    }

