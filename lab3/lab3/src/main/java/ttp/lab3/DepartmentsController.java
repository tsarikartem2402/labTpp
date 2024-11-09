package ttp.lab3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/departments")
public class DepartmentsController {

    @Autowired
    private DepartmentsRepository departmentsRepository;

    @GetMapping
    public List<Departments> getAllDepartments() {
        return departmentsRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Departments> getDepartmentById(@PathVariable int id) {
        return departmentsRepository.findById(id);
    }

    @PostMapping
    public String createDepartment(@RequestBody Departments department) {
        departmentsRepository.save(department);
        return "Department created successfully!";
    }

    @PutMapping("/{id}")
    public String updateDepartment(@PathVariable int id, @RequestBody Departments updatedDepartment) {
        updatedDepartment = new Departments(id, updatedDepartment.name(), updatedDepartment.description());
        departmentsRepository.update(updatedDepartment);
        return "Department updated successfully!";
    }

    @DeleteMapping("/{id}")
    public String deleteDepartment(@PathVariable int id) {
        departmentsRepository.deleteById(id);
        return "Department deleted successfully!";
    }
}
