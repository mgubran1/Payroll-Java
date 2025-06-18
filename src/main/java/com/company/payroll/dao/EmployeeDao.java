package com.company.payroll.dao;

import com.company.payroll.model.Employee;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {
    private final List<Employee> employees = new ArrayList<>();
    private int nextId = 1;

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees);
    }

    public Employee getEmployeeById(int id) {
        return employees.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void addEmployee(Employee employee) {
        employee.setId(nextId++);
        employees.add(employee);
    }

    public void updateEmployee(Employee employee) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == employee.getId()) {
                employees.set(i, employee);
                return;
            }
        }
    }

    public void deleteEmployee(int id) {
        employees.removeIf(e -> e.getId() == id);
    }
}