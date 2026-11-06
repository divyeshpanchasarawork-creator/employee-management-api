package org.divyesh.panchasara.employee_management_api.repository;

import org.divyesh.panchasara.employee_management_api.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("""
        SELECT e FROM Employee e
        WHERE (:department IS NULL OR e.department = :department)
        AND (:minSalary IS NULL OR e.salary >= :minSalary)
        AND (:maxSalary IS NULL OR e.salary <= :maxSalary)
        AND (:createdAt IS NULL OR e.createdAt = :createdAt)
    """)
    Page<Employee> filterEmployees(
            @Param("department") String department,
            @Param("minSalary") BigDecimal minSalary,
            @Param("maxSalary") BigDecimal maxSalary,
            @Param("createdAt") LocalDate createdAt,
            Pageable pageable
    );
}
