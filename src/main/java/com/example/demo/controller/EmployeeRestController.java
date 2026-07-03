package com.example.demo.controller;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeRestController {

    @Autowired
    private EmployeeRepository repository;

    // GET all employees
    @GetMapping
    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    // GET employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    // POST - Create new employee
    @PostMapping
    public Employee createEmployee(@Valid @RequestBody Employee employee) {
        if ("PROG".equalsIgnoreCase(employee.getDesignation())) {
            employee.setSalary(25000.0);
        } else if ("MANG".equalsIgnoreCase(employee.getDesignation())) {
            employee.setSalary(30000.0);
        } else if ("TEST".equalsIgnoreCase(employee.getDesignation())) {
            employee.setSalary(20000.0);
        }
        return repository.save(employee);
    }

    // PUT - Update employee
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee updatedEmployee) {
        return repository.findById(id)
                .map(emp -> {
                    emp.setName(updatedEmployee.getName());
                    emp.setAge(updatedEmployee.getAge());
                    emp.setDesignation(updatedEmployee.getDesignation());
                    emp.setSalary(updatedEmployee.getSalary());
                    return ResponseEntity.ok(repository.save(emp));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Delete employee
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok("Employee with ID " + id + " deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}
