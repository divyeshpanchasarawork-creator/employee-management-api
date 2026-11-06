package org.divyesh.panchasara.employee_management_api.service;

import org.divyesh.panchasara.employee_management_api.dto.request.CreateEmployeeRequest;
import org.divyesh.panchasara.employee_management_api.dto.request.EmployeePatchRequest;
import org.divyesh.panchasara.employee_management_api.dto.request.UpdateEmployeeRequest;
import org.divyesh.panchasara.employee_management_api.dto.response.EmployeeResponse;
import org.divyesh.panchasara.employee_management_api.entity.Employee;
import org.divyesh.panchasara.employee_management_api.exception.DuplicateEmailException;
import org.divyesh.panchasara.employee_management_api.exception.EmployeeNotFoundException;
import org.divyesh.panchasara.employee_management_api.exception.InvalidEmployeeFilterException;
import org.divyesh.panchasara.employee_management_api.repository.EmployeeRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // --------------------------------------------------
    // 1. CREATE EMPLOYEE
    // --------------------------------------------------

    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {

        String email = normalizeEmail(request.getEmail());

        if (employeeRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(
                    "Email is already assigned to another employee"
            );
        }

        Employee employee = new Employee();

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(email);
        employee.setAge(request.getAge());
        employee.setDepartment(request.getDepartment());
        employee.setSalary(request.getSalary());
        employee.setActive(true);

        Employee savedEmployee = employeeRepository.saveAndFlush(employee);

        return mapToResponse(savedEmployee);
    }

    // --------------------------------------------------
    // 2. GET EMPLOYEE BY ID
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployee(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found: " + id
                        )
                );

        return mapToResponse(employee);
    }

    // --------------------------------------------------
    // 3. GET FILTERED EMPLOYEES
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getFilteredEmployees(
            String department,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            Integer page,
            Integer size,
            String sortBy,
            String direction,
            LocalDate createdAt
    ) {

        // Validate salary range
        if (minSalary != null
                && maxSalary != null
                && minSalary.compareTo(maxSalary) > 0) {

            throw new InvalidEmployeeFilterException(
                    "minSalary cannot be greater than maxSalary"
            );
        }

        // Validate pagination
        if (page == null || page < 0) {
            throw new InvalidEmployeeFilterException(
                    "Page must be zero or greater"
            );
        }

        if (size == null || size < 1 || size > 100) {
            throw new InvalidEmployeeFilterException(
                    "Size must be between 1 and 100"
            );
        }

        // Validate sorting fields
        List<String> allowedSortFields = List.of(
                "id",
                "firstName",
                "lastName",
                "salary",
                "createdAt"
        );

        if (!allowedSortFields.contains(sortBy)) {
            throw new InvalidEmployeeFilterException(
                    "Invalid sortBy field"
            );
        }

        // Validate sorting direction
        if (direction == null
                || (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc"))) {

            throw new InvalidEmployeeFilterException(
                    "Direction must be either asc or desc"
            );
        }

        // Construct Sort
        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Sort sort = Sort.by(sortDirection, sortBy);

        // Construct Pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Execute database query
        Page<Employee> employeePage =
                employeeRepository.filterEmployees(
                        department,
                        minSalary,
                        maxSalary,
                        createdAt,
                        pageable
                );

        // Convert Employee entities into response DTOs
        return employeePage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // --------------------------------------------------
    // 4. FULL UPDATE (PUT)
    // --------------------------------------------------

    public EmployeeResponse putEmployee(
            Long id,
            UpdateEmployeeRequest request
    ) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found: " + id
                        )
                );

        String email = normalizeEmail(request.getEmail());

        // Check whether another employee owns this email
        if (employeeRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateEmailException(
                    "Email is already assigned to another employee"
            );
        }

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(email);
        employee.setAge(request.getAge());
        employee.setDepartment(request.getDepartment());
        employee.setSalary(request.getSalary());

        Employee updatedEmployee = employeeRepository.saveAndFlush(employee);

        return mapToResponse(updatedEmployee);
    }

    // --------------------------------------------------
    // 5. PARTIAL UPDATE (PATCH)
    // --------------------------------------------------

    public EmployeeResponse patchEmployee(
            Long id,
            EmployeePatchRequest request
    ) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found: " + id
                        )
                );

        if (request.getFirstName() != null) {
            employee.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            employee.setLastName(request.getLastName());
        }

        if (request.getEmail() != null) {

            String email = normalizeEmail(request.getEmail());

            if (employeeRepository.existsByEmailAndIdNot(email, id)) {
                throw new DuplicateEmailException(
                        "Email is already assigned to another employee"
                );
            }

            employee.setEmail(email);
        }

        if (request.getAge() != null) {
            employee.setAge(request.getAge());
        }

        if (request.getDepartment() != null) {
            employee.setDepartment(request.getDepartment());
        }

        if (request.getSalary() != null) {
            employee.setSalary(request.getSalary());
        }

        Employee updatedEmployee = employeeRepository.saveAndFlush(employee);

        return mapToResponse(updatedEmployee);
    }

    // --------------------------------------------------
    // 6. DELETE EMPLOYEE
    // --------------------------------------------------

    public void deleteEmployee(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found: " + id
                        )
                );

        employeeRepository.delete(employee);
    }

    // --------------------------------------------------
    // HELPER METHODS
    // --------------------------------------------------

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private EmployeeResponse mapToResponse(Employee employee) {

        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getAge(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.getCreatedAt(),
                employee.getActive()
        );
    }
}
