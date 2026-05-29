package com.ipesapinturas.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DATABASE = "pinturadb";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "2439";
    private static final String DEFAULT_OPTIONS = "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String CONFIG_FILE = "db.properties";

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                DatabaseConfig config = DatabaseConfig.load();
                connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
                System.out.println("Conexion exitosa a la base de datos: " + config.getUrlWithoutCredentials());
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

    private static class DatabaseConfig {
        private final String url;
        private final String user;
        private final String password;

        private DatabaseConfig(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }

        private static DatabaseConfig load() {
            Properties properties = loadPropertiesFile();
            String host = readSetting(properties, "db.host", "DB_HOST", DEFAULT_HOST);
            String port = readSetting(properties, "db.port", "DB_PORT", DEFAULT_PORT);
            String database = readSetting(properties, "db.name", "DB_NAME", DEFAULT_DATABASE);
            String options = readSetting(properties, "db.options", "DB_OPTIONS", DEFAULT_OPTIONS);
            String generatedUrl = buildJdbcUrl(host, port, database, options);
            String url = readSetting(properties, "db.url", "DB_URL", generatedUrl);
            String user = readSetting(properties, "db.user", "DB_USER", DEFAULT_USER);
            String password = readSetting(properties, "db.password", "DB_PASSWORD", DEFAULT_PASSWORD);
            return new DatabaseConfig(url, user, password);
        }

        private static Properties loadPropertiesFile() {
            Properties properties = new Properties();
            Path configPath = Paths.get(CONFIG_FILE);

            if (!Files.exists(configPath)) {
                return properties;
            }

            try (InputStream input = Files.newInputStream(configPath)) {
                properties.load(input);
            } catch (IOException e) {
                System.err.println("No se pudo leer " + CONFIG_FILE + ": " + e.getMessage());
            }

            return properties;
        }

        private static String readSetting(Properties properties, String propertyName,
                                          String environmentName, String defaultValue) {
            String systemValue = System.getProperty(propertyName);
            if (hasText(systemValue)) {
                return systemValue.trim();
            }

            String environmentValue = System.getenv(environmentName);
            if (hasText(environmentValue)) {
                return environmentValue.trim();
            }

            String fileValue = properties.getProperty(propertyName);
            if (hasText(fileValue)) {
                return fileValue.trim();
            }

            return defaultValue;
        }

        private static boolean hasText(String value) {
            return value != null && !value.trim().isEmpty();
        }

        private static String buildJdbcUrl(String host, String port, String database, String options) {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            if (hasText(options)) {
                url += "?" + options;
            }
            return url;
        }

        private String getUrl() {
            return url;
        }

        private String getUser() {
            return user;
        }

        private String getPassword() {
            return password;
        }

        private String getUrlWithoutCredentials() {
            return url;
        }
    }
}
