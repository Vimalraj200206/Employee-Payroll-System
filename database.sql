CREATE DATABASE IF NOT EXISTS employee_payroll;
USE employee_payroll;

CREATE TABLE IF NOT EXISTS employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(80) NOT NULL,
    designation VARCHAR(80) NOT NULL,
    basic_salary DECIMAL(10,2) NOT NULL,
    allowances DECIMAL(10,2) NOT NULL DEFAULT 0,
    deductions DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Optional sample records:
INSERT INTO employees (name, department, designation, basic_salary, allowances, deductions)
VALUES
('Arun Kumar', 'IT', 'Junior Developer', 25000.00, 3000.00, 1000.00),
('Priya', 'Accounts', 'Assistant', 30000.00, 4000.00, 2000.00);
