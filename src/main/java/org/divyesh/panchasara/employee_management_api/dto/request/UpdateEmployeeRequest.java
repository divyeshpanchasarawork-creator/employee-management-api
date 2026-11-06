package org.divyesh.panchasara.employee_management_api.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class UpdateEmployeeRequest {
    @NotBlank(message = "First name shall not be empty/blank/null")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;


    @NotBlank(message = "Last name shall not be empty/blank/null")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Age cannot be null")
    @Min(value = 18, message = "Employee must be at least 18 years old")
    @Max(value = 65, message = "Employee age cannot exceed 65 years")
    private Integer age;

    @NotBlank(message = "Department shall not be empty/blank/null")
    private String department;

    @NotNull(message = "Salary cannot be null")
    @DecimalMin(value = "15000.00", message = "Minimum salary shall be 15000")
    private BigDecimal salary;

    public UpdateEmployeeRequest() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
}
