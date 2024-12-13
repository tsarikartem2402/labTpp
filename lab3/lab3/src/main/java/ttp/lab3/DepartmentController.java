package ttp.lab3;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/departments")
public class DepartmentController {
    
    @Autowired
    private DepartmentService departmentService;


    @GetMapping
    public String showDepartmentPage(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments"; 
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public String saveDepartment(@ModelAttribute Departments department, Model model) {
        String result = departmentService.saveDepartment(department);
        model.addAttribute("message", result);
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments";
    }

    
    @PostMapping("/search")
    public String searchDepartment(@RequestParam("searchId") int id, Model model) {
        Departments department = departmentService.findDepartmentById(id);
        if (department != null) {
            model.addAttribute("department", department);
            model.addAttribute("message", "Department found!");
        } else {
            model.addAttribute("error", "Department with ID " + id + " not found.");
        }
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments";
    }

    // Handle department search by name
    @PostMapping("/search/name")
    public String searchDepartmentByName(@RequestParam("searchName") String name, Model model) {
        List<Departments> foundDepartments = departmentService.findDepartmentsByName(name);
        if (!foundDepartments.isEmpty()) {
            model.addAttribute("departments", foundDepartments);
            model.addAttribute("message", "Found " + foundDepartments.size() + " departments matching '" + name + "'");
        } else {
            model.addAttribute("error", "No departments found matching '" + name + "'");
            model.addAttribute("departments", departmentService.getAllDepartments());
        }
        return "departments";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public String deleteDepartment(@RequestParam("deleteId") int id, Model model) {
        String result = departmentService.deleteDepartment(id);
        if (result.contains("successfully")) {
            model.addAttribute("message", result);
        } else {
            model.addAttribute("error", result);
        }
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments";
    }

    // Handle exceptions
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        model.addAttribute("error", "An error occurred: " + e.getMessage());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments";
    }
}