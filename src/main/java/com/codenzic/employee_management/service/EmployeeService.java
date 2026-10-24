package com.codenzic.employee_management.service;


import com.codenzic.employee_management.dto.EmployeeRequest;
import com.codenzic.employee_management.dto.EmployeeResponse;
import com.codenzic.employee_management.entity.Employee;
import com.codenzic.employee_management.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.codenzic.employee_management.exception.ResourceNotFoundException;
import com.codenzic.employee_management.exception.DuplicateResourceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Locale;



@Service
@Transactional
public class EmployeeService {

    public Page<EmployeeResponse> searchEmployees(
            String search,
            Boolean active,
            int page,
            int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        // Treat an empty search value as no search filter
        if (search != null && search.isBlank()) {
            search = null;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").ascending());

        return employeeRepository
                .searchEmployees(search, active, pageable)
                .map(employee -> new EmployeeResponse(
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getEmail(),
                        employee.getDepartment(),
                        employee.getSalary(),
                        employee.isActive()));
    }
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    public EmployeeResponse createEmployee(EmployeeRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (employeeRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "An employee with this email already exists");
        }

        Employee employee = new Employee();
        copyRequestToEmployee(request, employee);
        employee.setEmail(email);

        Employee savedEmployee = employeeRepository.save(employee);
        return toResponse(savedEmployee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = findEmployee(id);
        return toResponse(employee);
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = findEmployee(id);
        String email = normalizeEmail(request.getEmail());

        if (employeeRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateResourceException(
                    "An employee with this email already exists");
        }

        copyRequestToEmployee(request, employee);
        employee.setEmail(email);

        Employee updatedEmployee = employeeRepository.save(employee);
        return toResponse(updatedEmployee);
    }

    public void deleteEmployee(Long id) {
        Employee employee = findEmployee(id);
        employeeRepository.delete(employee);
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + id));
    }

    private void copyRequestToEmployee(
            EmployeeRequest request, Employee employee) {

        employee.setFirstName(request.getFirstName().trim());
        employee.setLastName(request.getLastName().trim());
        employee.setEmail(normalizeEmail(request.getEmail()));
        employee.setDepartment(request.getDepartment().trim());
        employee.setSalary(request.getSalary());
        employee.setActive(request.getActive());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.isActive()
        );
    }
}
