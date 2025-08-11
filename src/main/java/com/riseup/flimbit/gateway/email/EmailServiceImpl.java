package com.riseup.flimbit.gateway.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.gateway_sms.TwoFactorSmsGatewayService;
import com.riseup.flimbit.service.SystemSettingsService;

import java.io.File;
import java.util.Objects;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {
	Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired
	private SystemSettingsService settingsService;

	@Value("${default_active_email_gateway}")
	String defaultProvider;

	// ✅ Build JavaMailSender dynamically from DB
	private JavaMailSender createDynamicMailSender() {

		logger.info("+++++++ java mail starts ******");
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		String activeProvider = Objects.requireNonNullElse(
				settingsService.getValue("default_active_email_gateway", EntityName.EMAIL.name()), defaultProvider);
		System.out.println("active eamil gateway :" + activeProvider);

		String smtpHost = Objects.requireNonNullElse(
				settingsService.getValue(activeProvider + "_" + "smtp_host", EntityName.EMAIL.name()), "not available");

		String smtpPassword = Objects.requireNonNullElse(
				settingsService.getValue(activeProvider + "_" + "smtp_password", EntityName.EMAIL.name()),
				"not available ");

		String smtpPort = Objects.requireNonNullElse(
				settingsService.getValue(activeProvider + "_" + "smtp_port", EntityName.EMAIL.name()), "0");

		String smtpUsername = Objects.requireNonNullElse(
				settingsService.getValue(activeProvider + "_" + "smtp_username", EntityName.EMAIL.name()),
				"not available");

		logger.info("active profile " + activeProvider + ": host : " + smtpHost + " :port :" + smtpPort + ":user :"
				+ smtpUsername + ":pasword " + smtpPassword);

		mailSender.setHost(smtpHost);
		mailSender.setPort(Integer.parseInt(smtpPort));
		mailSender.setUsername(smtpUsername);
		mailSender.setPassword(smtpPassword);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "false");

		return mailSender;
	}

	@Override
	public void sendVerificationEmail(String to, String code) throws MessagingException {
		JavaMailSender mailSender = createDynamicMailSender(); // ✅ Load fresh config
		MimeMessage message = mailSender.createMimeMessage();

		

		String messageBody = "<p>Your verification code is:</p>" + "<h2 style='color:#2E86C1;'>" + code + "</h2>"
				+ "<p>Enter this code in the app to verify your email address.</p>"
				+ "<p><b>Note:</b> This code will expire in 10 minutes.</p>";

		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(to);
		helper.setSubject("Your Email Verification Code - FilmBit");
		helper.setText(messageBody, true);


		mailSender.send(message);
		System.out.println("✅ Verification email sent to: " + to);
	}

	@Override
	public void sendEmailWithAttachment(String to, String subject, String text, File attachment)
			throws MessagingException {
		JavaMailSender mailSender = createDynamicMailSender(); // ✅ Load fresh config
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(text, false);

		if (attachment != null && attachment.exists()) {
			helper.addAttachment(attachment.getName(), attachment);
		}

		mailSender.send(message);
		System.out.println("✅ Email with attachment sent to: " + to);
	}
}
