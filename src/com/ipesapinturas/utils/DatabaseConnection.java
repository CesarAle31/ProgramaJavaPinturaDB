package com.ipesapinturas.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/pinturadb";
    private static final String USER = "root";
    private static final String PASSWORD = "2439";

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
}
