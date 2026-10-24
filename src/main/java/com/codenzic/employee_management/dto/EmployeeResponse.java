package com.codenzic.employee_management.dto;

import java.math.BigDecimal;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String department,
        BigDecimal salary,
        boolean active
) {
}


