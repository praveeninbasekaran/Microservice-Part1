package com.dbtojson;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A generic data processor to fetch data from a database and map it to a DTO
 * using reflection.
 *
 * @param <T> The DTO class.
 */
public class GenericDataProcessor<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void processData(
            String dbUrl,
            String dbUser,
            String dbPassword,
            String dbDriver,
            String fetchSql,
            String updateSql,
            Class<T> dtoClass) {

        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found: " + dbDriver);
            e.printStackTrace();
            return;
        }

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            System.out.println("Database connection established successfully.");

            // Step 1: Fetch data from the database as a list of maps
            List<Map<String, Object>> fetchedData = fetch(conn, fetchSql);
            System.out.println("Fetched " + fetchedData.size() + " records.");

            // Step 2: Iterate through each row, map it to a DTO, and generate JSON
            for (Map<String, Object> row : fetchedData) {
                T dto = mapToDto(row, dtoClass);
                String json = objectMapper.writeValueAsString(dto);
                System.out.println("Generated JSON for record: " + json);

                // Step 3: Update the database with the generated JSON string
                update(conn, updateSql, row, json);
            }

            System.out.println("Data processing completed successfully.");

        } catch (SQLException e) {
            System.err.println("Database error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executes the fetch SQL query and returns the results as a list of maps.
     */
    private List<Map<String, Object>> fetch(Connection conn, String fetchSql) throws SQLException {
        List<Map<String, Object>> data = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(fetchSql);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i).toLowerCase();
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                data.add(row);
            }
        }
        return data;
    }

    /**
     * Maps a single database row (Map) to a DTO using reflection.
     * This method handles both simple columns and a JSON 'data' column.
     */
    private T mapToDto(Map<String, Object> row, Class<T> dtoClass) throws Exception {
        T dto = dtoClass.getConstructor().newInstance();

        // Handle the 'data' JSONB column separately if it exists
        if (row.containsKey("data")) {
            String jsonString = row.get("data").toString();
            objectMapper.readerForUpdating(dto).readValue(jsonString);
            row.remove("data"); // Remove to avoid processing twice
        }

        // Map all remaining columns to DTO fields using reflection
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String columnName = entry.getKey();
            Object value = entry.getValue();

            try {
                String fieldName = toCamelCase(columnName);
                String setterMethodName = "set" + capitalize(fieldName);

                Method setter = findSetter(dtoClass, setterMethodName);
                if (setter != null) {
                    Class<?> paramType = setter.getParameterTypes()[0];
                    Object castValue = castToType(value, paramType);

                    if (castValue != null) {
                        setter.invoke(dto, castValue);
                    } else {
                        System.err.println("Warning: Failed to cast value for column " + columnName);
                    }
                } else {
                    System.err.println("Warning: No setter found for column " + columnName + " after conversion.");
                }
            } catch (Exception e) {
                System.err.println("Error mapping column " + columnName + " to DTO field: " + e.getMessage());
            }
        }
        return dto;
    }

    private String toCamelCase(String snakeCase) {
        StringBuilder camelCaseBuilder = new StringBuilder();
        String[] parts = snakeCase.split("_");
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                camelCaseBuilder.append(parts[i]);
            } else {
                camelCaseBuilder.append(capitalize(parts[i]));
            }
        }
        return camelCaseBuilder.toString();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private Method findSetter(Class<?> clazz, String setterMethodName) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(setterMethodName) && method.getParameterCount() == 1) {
                return method;
            }
        }
        return null;
    }

    private Object castToType(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.isInstance(value)) {
            return value;
        }

        if (targetType.equals(String.class)) {
            return value.toString();
        } else if (targetType.equals(Integer.class) && value instanceof Number) {
            return ((Number) value).intValue();
        } else if (targetType.equals(Long.class) && value instanceof Number) {
            return ((Number) value).longValue();
        } else if (targetType.equals(Double.class) && value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (targetType.equals(Float.class) && value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return null;
    }

    /**
     * Updates the database with the generated JSON string.
     */
    private void update(Connection conn, String updateSql, Map<String, Object> row, String json) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            Object primaryKey = row.get("id");
            if (primaryKey == null) {
                primaryKey = row.get("user_id");
            }
            if (primaryKey == null) {
                primaryKey = row.get("employee_id");
            }

            if (primaryKey == null) {
                throw new SQLException("Primary key not found in the fetched data.");
            }

            stmt.setString(1, json);
            stmt.setObject(2, primaryKey);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Updated " + rowsAffected + " rows for primary key: " + primaryKey);
        }
    }
}
