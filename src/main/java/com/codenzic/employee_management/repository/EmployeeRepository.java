package com.codenzic.employee_management.repository;

import com.codenzic.employee_management.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Check whether an email already exists
    boolean existsByEmail(String email);

    // Check whether an email belongs to another employee
    boolean existsByEmailAndIdNot(String email, Long id);

    // Search employees by name, email, or department,
    // with an optional active-status filter and pagination
    @Query("""
            SELECT e FROM Employee e
            WHERE (
                :search IS NULL
                OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(e.department) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND (
                :active IS NULL
                OR e.active = :active
            )
            """)
    Page<Employee> searchEmployees(
            @Param("search") String search,
            @Param("active") Boolean active,
            Pageable pageable
    );
}