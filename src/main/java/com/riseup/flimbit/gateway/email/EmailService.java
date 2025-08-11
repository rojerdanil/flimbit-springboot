package com.riseup.flimbit.gateway.email;

import java.io.File;

public interface EmailService {
	
	   void sendVerificationEmail(String to, String verificationLink) throws Exception;
	    void sendEmailWithAttachment(String to, String subject, String text, File attachment) throws Exception;


}
