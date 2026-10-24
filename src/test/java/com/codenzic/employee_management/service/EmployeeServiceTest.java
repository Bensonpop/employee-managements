
package com.codenzic.employee_management.service;

import com.codenzic.employee_management.dto.EmployeeRequest;
import com.codenzic.employee_management.dto.EmployeeResponse;
import com.codenzic.employee_management.entity.Employee;
import com.codenzic.employee_management.exception.DuplicateResourceException;
import com.codenzic.employee_management.exception.ResourceNotFoundException;
import com.codenzic.employee_management.repository.EmployeeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeRequest request;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("Benson");
        employee.setLastName("Benny");
        employee.setEmail("benson@example.com");
        employee.setDepartment("IT");
        employee.setSalary(new BigDecimal("35000.00"));
        employee.setActive(true);

        request = new EmployeeRequest();
        request.setFirstName("Benson");
        request.setLastName("Benny");
        request.setEmail("benson@example.com");
        request.setDepartment("IT");
        request.setSalary(new BigDecimal("35000.00"));
        request.setActive(true);
    }

    @Test
    void createEmployee_shouldReturnEmployee_whenValidRequest() {
        when(employeeRepository.existsByEmail("benson@example.com"))
                .thenReturn(false);
        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(employee);

        EmployeeResponse response =
                employeeService.createEmployee(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Benson", response.firstName());
        assertEquals("benson@example.com", response.email());

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createEmployee_shouldThrowException_whenEmailAlreadyExists() {
        when(employeeRepository.existsByEmail("benson@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> employeeService.createEmployee(request)
        );

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void getAllEmployees_shouldReturnEmployees() {
        when(employeeRepository.findAll())
                .thenReturn(List.of(employee));

        List<EmployeeResponse> responses =
                employeeService.getAllEmployees();

        assertEquals(1, responses.size());
        assertEquals("Benson", responses.get(0).firstName());
        verify(employeeRepository).findAll();
    }

    @Test
    void getEmployeeById_shouldReturnEmployee_whenFound() {
        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        EmployeeResponse response =
                employeeService.getEmployeeById(1L);

        assertEquals(1L, response.id());
        assertEquals("Benson", response.firstName());
    }

    @Test
    void getEmployeeById_shouldThrowException_whenNotFound() {
        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(99L)
        );
    }

    @Test
    void updateEmployee_shouldUpdateEmployee_whenValidRequest() {
        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmailAndIdNot(
                "benson@example.com", 1L))
                .thenReturn(false);
        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        request.setDepartment("Finance");

        EmployeeResponse response =
                employeeService.updateEmployee(1L, request);

        assertEquals("Finance", response.department());
        verify(employeeRepository).save(employee);
    }

    @Test
    void updateEmployee_shouldThrowException_whenEmailAlreadyUsed() {
        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmailAndIdNot(
                "benson@example.com", 1L))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> employeeService.updateEmployee(1L, request)
        );

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_shouldDeleteEmployee_whenFound() {
        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).delete(employee);
    }

    @Test
    void deleteEmployee_shouldThrowException_whenNotFound() {
        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.deleteEmployee(99L)
        );

        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    void searchEmployees_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(
                0, 10, Sort.by("id").ascending());

        Page<Employee> employeePage =
                new PageImpl<>(List.of(employee));

        when(employeeRepository.searchEmployees(
                "Benson", true, pageable))
                .thenReturn(employeePage);

        Page<EmployeeResponse> result =
                employeeService.searchEmployees("Benson", true, 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("Benson", result.getContent().get(0).firstName());
    }

    @Test
    void searchEmployees_shouldRejectNegativePage() {
        assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.searchEmployees(null, null, -1, 10)
        );
    }

    @Test
    void searchEmployees_shouldRejectInvalidPageSize() {
        assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.searchEmployees(null, null, 0, 101)
        );
    }
}
