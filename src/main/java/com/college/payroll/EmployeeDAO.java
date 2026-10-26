package com.college.payroll;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO = Data Access Object: database operations live in this class. */
public class EmployeeDAO {
    public List<Employee> getAll() throws SQLException {
        String sql = "SELECT * FROM employees ORDER BY id";
        List<Employee> employees = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) employees.add(fromResultSet(rs));
        }
        return employees;
    }

    public void add(String name, String department, String designation,
                    BigDecimal basic, BigDecimal allowances, BigDecimal deductions) throws SQLException {
        String sql = "INSERT INTO employees(name, department, designation, basic_salary, allowances, deductions) VALUES(?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            fill(ps, name, department, designation, basic, allowances, deductions);
            ps.executeUpdate();
        }
    }

    public void update(int id, String name, String department, String designation,
                       BigDecimal basic, BigDecimal allowances, BigDecimal deductions) throws SQLException {
        String sql = "UPDATE employees SET name=?, department=?, designation=?, basic_salary=?, allowances=?, deductions=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            fill(ps, name, department, designation, basic, allowances, deductions);
            ps.setInt(7, id);
            if (ps.executeUpdate() == 0) throw new SQLException("No employee found with ID " + id);
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM employees WHERE id=?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) throw new SQLException("No employee found with ID " + id);
        }
    }

    private void fill(PreparedStatement ps, String name, String department, String designation,
                      BigDecimal basic, BigDecimal allowances, BigDecimal deductions) throws SQLException {
        ps.setString(1, name);
        ps.setString(2, department);
        ps.setString(3, designation);
        ps.setBigDecimal(4, basic);
        ps.setBigDecimal(5, allowances);
        ps.setBigDecimal(6, deductions);
    }

    private Employee fromResultSet(ResultSet rs) throws SQLException {
        return new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("department"),
                rs.getString("designation"), rs.getBigDecimal("basic_salary"),
                rs.getBigDecimal("allowances"), rs.getBigDecimal("deductions"));
    }
}
