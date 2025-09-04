package com.dbtojson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.dbtojson.model.Person;
import com.dbtojson.model.Product;

public class DataProcessor {
	
	 public static void main(String[] args) {
	        System.out.println("Starting modular data processing...");

	        String configFileName = "config.properties";

	        try {
	            // Read common database properties once
	            Properties config = readConfig(configFileName);
	            String[] modules = config.getProperty("modules").split(",");
	            String commonDbUrl = config.getProperty("dbUrl");
	            String commonDbUser = config.getProperty("dbUser");
	            String commonDbPassword = config.getProperty("dbPassword");
	            String commonDbDriver = config.getProperty("dbDriver");
	            
	            for (String module : modules) {
	                String moduleName = module.trim();
	                System.out.println("\n--- Processing module: " + moduleName + " ---");

	                // Get module-specific properties
	                String fetchSql = config.getProperty(moduleName + ".fetchSql");
	                String updateSql = config.getProperty(moduleName + ".updateSql");
	                String dtoClassName = config.getProperty(moduleName + ".dtoClass");

	                // Dynamically load the DTO class using reflection
	                Class<?> dtoClass = Class.forName(dtoClassName);

	                // Create a new GenericDataProcessor and process the data for this module
	                GenericDataProcessor<?> processor = new GenericDataProcessor<>();

	                // Note: We need a temporary variable to handle the generic type
	                // since it's determined at runtime.
	                if (dtoClass.equals(Person.class)) {
	                    ((GenericDataProcessor<Person>) processor).processData(
	                        commonDbUrl, commonDbUser, commonDbPassword, commonDbDriver, fetchSql, updateSql, Person.class
	                    );
	                } else if (dtoClass.equals(Product.class)) {
	                    ((GenericDataProcessor<Product>) processor).processData(
	                        commonDbUrl, commonDbUser, commonDbPassword, commonDbDriver, fetchSql, updateSql, Product.class
	                    );
	                } else {
	                    System.err.println("Unsupported DTO class for module " + moduleName + ": " + dtoClassName);
	                }
	            }

	        } catch (Exception e) {
	            System.err.println("An error occurred during data processing:");
	            e.printStackTrace();
	        }
	    }

	    /**
	     * Reads properties from the classpath.
	     * @param fileName The name of the properties file on the classpath.
	     * @return A Properties object containing the configuration.
	     * @throws IOException If the file cannot be read.
	     */
	    private static Properties readConfig(String fileName) throws IOException {
	        Properties prop = new Properties();
	        try (InputStream input = ClassLoader.getSystemResourceAsStream(fileName)) {
	            if (input == null) {
	                throw new IOException("Resource not found on classpath: " + fileName);
	            }
	            prop.load(input);
	        }
	        return prop;
	    }
}
