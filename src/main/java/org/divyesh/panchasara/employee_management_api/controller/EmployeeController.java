package org.divyesh.panchasara.employee_management_api.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import org.divyesh.panchasara.employee_management_api.dto.request.CreateEmployeeRequest;
import org.divyesh.panchasara.employee_management_api.dto.request.EmployeePatchRequest;
import org.divyesh.panchasara.employee_management_api.dto.request.UpdateEmployeeRequest;
import org.divyesh.panchasara.employee_management_api.dto.response.EmployeeResponse;
import org.divyesh.panchasara.employee_management_api.service.EmployeeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest employeeRequest) {
        EmployeeResponse employeeResponse = service.createEmployee(employeeRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(employeeResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getService(@PathVariable Long id) {
        EmployeeResponse employeeResponse = service.getEmployee(id);

        return ResponseEntity.status(HttpStatus.OK).body(employeeResponse);
    }


    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getFilteredEmployees(

            @RequestParam(required = false)
            String department,

            @RequestParam(required = false)
            @PositiveOrZero
            BigDecimal minSalary,

            @RequestParam(required = false)
            @PositiveOrZero
            BigDecimal maxSalary,

            @RequestParam(defaultValue = "0")
            @Min(0)
            Integer page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            Integer size,

            @RequestParam(defaultValue = "lastName")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate createdAt
    ) {

        if (minSalary != null && maxSalary != null && minSalary.compareTo(maxSalary) > 0) {
            throw new IllegalArgumentException(
                    "minSalary cannot be greater than maxSalary"
            );
        }

        List<String> allowedSortFields = List.of(
                "firstName", "lastName", "salary", "createdAt"
        );

        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sortBy field");
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException(
                    "Direction must be either asc or desc"
            );
        }

        List<EmployeeResponse> employees =
                service.getFilteredEmployees(
                        department,
                        minSalary,
                        maxSalary,
                        page,
                        size,
                        sortBy,
                        direction,
                        createdAt
                );

        return ResponseEntity.ok(employees);
    }


    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> putEmployee(@PathVariable Long id, @Valid @RequestBody UpdateEmployeeRequest updateEmployee) {
        EmployeeResponse employeeResponse = service.putEmployee(id, updateEmployee);

        return ResponseEntity.status(HttpStatus.OK).body(employeeResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeResponse> patchEmployee(@PathVariable Long id, @Valid @RequestBody EmployeePatchRequest patchEmployee) {
        EmployeeResponse employeeResponse = service.patchEmployee(id, patchEmployee);

        return ResponseEntity.status(HttpStatus.OK).body(employeeResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        service.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }
}
