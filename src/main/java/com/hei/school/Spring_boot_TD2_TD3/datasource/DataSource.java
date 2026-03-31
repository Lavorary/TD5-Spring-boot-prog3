package com.hei.school.Spring_boot_TD2_TD3.datasource;

import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DataSource {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/mini_dish_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "lavorary";

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }
}