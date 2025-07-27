package com.riseup.flimbit.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Autowired
    private SystemSettingsService systemSettingsService;

    public boolean sendSms(String phoneNumber, String message) {
        // Fetch the active SMS gateway provider
        String activeGateway = systemSettingsService.getValue("active_sms_gateway", "SMS");

        // Based on the active provider, fetch the respective API URL and API Key
        String apiUrl = systemSettingsService.getValue(activeGateway + "_sms_gateway_url", "SMS");
        String apiKey = systemSettingsService.getValue(activeGateway + "_sms_gateway_key", "SMS");

        // Now you have the correct provider's URL and API Key, and you can send the SMS
        try {
            URL url = new URL(apiUrl + "/messages");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);

            String postData = "to=" + phoneNumber + "&message=" + message;
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = postData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK; // return success/failure
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        
        
    }
}
