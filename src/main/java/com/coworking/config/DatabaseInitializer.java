package com.coworking.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements ApplicationRunner {
    private final DatabaseProperties databaseProperties;

    public DatabaseInitializer(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ensureDatabaseExists();
        executeSqlScript("schema.sql");
        executeSqlScript("data.sql");
    }

    private void ensureDatabaseExists() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                databaseProperties.getServerUrl(),
                databaseProperties.getUsername(),
                databaseProperties.getPassword()
            );
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE IF NOT EXISTS coworking_space");
        }
    }

    private void executeSqlScript(String resourceName) throws IOException, SQLException {
        ClassPathResource resource = new ClassPathResource(resourceName);
        String script = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        try (Connection connection = DriverManager.getConnection(
                databaseProperties.getUrl(),
                databaseProperties.getUsername(),
                databaseProperties.getPassword()
            );
             Statement statement = connection.createStatement()) {
            Arrays.stream(script.split(";"))
                .map(String::trim)
                .filter(sql -> !sql.isEmpty())
                .forEach(sql -> executeStatement(statement, sql));
        }
    }

    private void executeStatement(Statement statement, String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to execute SQL statement: " + sql, ex);
        }
    }
}
