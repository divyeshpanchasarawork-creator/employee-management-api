package org.divyesh.panchasara.employee_management_api.repository;

import org.divyesh.panchasara.employee_management_api.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
