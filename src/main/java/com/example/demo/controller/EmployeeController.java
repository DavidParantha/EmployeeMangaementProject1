package com.example.demo.controller;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeRepository repository;

    // 1. Home page with 4 buttons
    @GetMapping("/")
    public String home() {
        return "home";
    }

    // 2. Create Employee page (GET)
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "create";
    }

    // Create Employee submission (POST)
    @PostMapping("/create")
    public String createEmployee(@Valid @ModelAttribute Employee employee, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "create";
        }
        if ("PROG".equalsIgnoreCase(employee.getDesignation())) {
            employee.setSalary(25000.0);
        } else if ("MANG".equalsIgnoreCase(employee.getDesignation())) {
            employee.setSalary(30000.0);
        } else if ("TEST".equalsIgnoreCase(employee.getDesignation())) {
            employee.setSalary(20000.0);
        } else {
            employee.setSalary(0.0);
        }
        repository.save(employee);
        return "redirect:/continue";
    }

    // 3. Display Employees page (GET)
    @GetMapping("/display")
    public String displayEmployees(Model model) {
        model.addAttribute("employees", repository.findAll());
        return "display";
    }

    // 4. Raise Salary page (GET)
    @GetMapping("/raise-salary")
    public String raiseSalaryForm() {
        return "raise-salary";
    }

    // Raise Salary submission (POST)
    @PostMapping("/raise-salary")
    public String raiseSalary(@RequestParam("name") String name, @RequestParam("amount") Double amount, Model model) {
        Employee emp = repository.findFirstByName(name).orElse(null);
        if (emp != null) {
            emp.setSalary(emp.getSalary() + amount);
            repository.save(emp);
            return "redirect:/continue";
        } else {
            model.addAttribute("error", "Employee Name '" + name + "' not found!");
            return "raise-salary";
        }
    }

    // 5. Continue Prompt page
    @GetMapping("/continue")
    public String continuePrompt() {
        return "continue";
    }

    // 6. Exit page
    @GetMapping("/exit")
    public String exitPage() {
        return "exit";
    }
}
