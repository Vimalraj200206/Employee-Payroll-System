package com.college.payroll;

import java.math.BigDecimal;

public class Employee {
    private final int id;
    private final String name;
    private final String department;
    private final String designation;
    private final BigDecimal basicSalary;
    private final BigDecimal allowances;
    private final BigDecimal deductions;

    public Employee(int id, String name, String department, String designation,
                    BigDecimal basicSalary, BigDecimal allowances, BigDecimal deductions) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.designation = designation;
        this.basicSalary = basicSalary;
        this.allowances = allowances;
        this.deductions = deductions;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }
    public BigDecimal getBasicSalary() { return basicSalary; }
    public BigDecimal getAllowances() { return allowances; }
    public BigDecimal getDeductions() { return deductions; }
    public BigDecimal getNetSalary() {
        return basicSalary.add(allowances).subtract(deductions);
    }
}
