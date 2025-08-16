package com.riseup.flimbit.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class DatabaseTestRunner implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Attempting to connect to DB: " + dbUrl);

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("DB Connection SUCCESSFUL ✅");
            } else {
                System.out.println("DB Connection FAILED ❌");
            }
        } catch (Exception e) {
            System.out.println("DB Connection ERROR ❌: " + e.getMessage());
        }
    }
}
