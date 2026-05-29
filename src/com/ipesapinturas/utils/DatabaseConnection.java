package com.ipesapinturas.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/pinturadb?useSSL=false&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=America/Mexico_City&useUnicode=true&characterEncoding=UTF-8";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "2439";

    private static final String URL = getConfig("IPESA_DB_URL", DEFAULT_URL);
    private static final String USER = getConfig("IPESA_DB_USER", DEFAULT_USER);
    private static final String PASSWORD = getConfig("IPESA_DB_PASSWORD", DEFAULT_PASSWORD);

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexion exitosa a la base de datos");
            }
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL no encontrado: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Error de conexion a la base de datos: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexion cerrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexion: " + e.getMessage());
        }
    }

    private static String getConfig(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }
}
