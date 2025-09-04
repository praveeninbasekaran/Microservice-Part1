package com.dbtojson.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class Employee {

    @JsonProperty("employeeId")
    private Long employeeId;
    
    @JsonProperty("employeeName")
    private String employeeName;
    
    @JsonProperty("employeeAge")
    private Integer employeeAge;
    
    @JsonProperty("employeePhone")
    private List<String> employeePhone;

    @JsonProperty("employeeAddress")
    private Address employeeAddress;
    
    // Add this no-arg constructor to allow instantiation by the GenericDataProcessor
    public Employee() {}

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setEmployeeAge(Integer employeeAge) {
        this.employeeAge = employeeAge;
    }
    
    public void setEmployeePhone(List<String> employeePhone) {
        this.employeePhone = employeePhone;
    }

    public void setEmployeeAddress(Address employeeAddress) {
        this.employeeAddress = employeeAddress;
    }

    // Fix: Add 'static' keyword here
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Address {
        private String street;
        private String city;
        private String zipCode;

        public String getStreet() { return street; }
        public String getCity() { return city; }
        public String getZipCode() { return zipCode; }

        public void setStreet(String street) { this.street = street; }
        public void setCity(String city) { this.city = city; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    }
}