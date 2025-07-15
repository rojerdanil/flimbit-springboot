package com.riseup.flimbit.entity.dto;

import java.sql.Timestamp;

public interface PayoutDTO {
	
	  // Movie and Language Information
    String getMovieName();
    String getLangName();

    // Investment Information
    Double getInvestAmount();
    Double getReturnAmount();
    String getStatus();  // Investment Status (e.g., Active, Paid, etc.)
    Integer getTotalShareType(); // Number of different share types
    String getPhoneNumber();  // User's phone number
    Integer getTotalShares();  // Total shares purchased
    Integer getMovieId();  // Movie ID
    Integer getUserId();  // User ID

    // Payout Information
    Double getPayAmount();  // Amount paid
    String getPayStatus();  // Payout Status (Paid, Pending, Failed)
    String getPayMethod();  // Payment method (e.g., UPI, Bank Transfer, etc.)
    String getPaidCreated();  // Date and time when the payout was created
    String getPaidUpdate();  // Date a
    Integer getShareTypeId();
    String getShareTypeName();
    String getFirstName();
    String getLastName();
    int getPerShareAmount();

}
