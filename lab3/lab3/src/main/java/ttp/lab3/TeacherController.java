package ttp.lab3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Teacher> getTeacherById(@PathVariable int id) {
        return teacherRepository.findById(id);
    }

    @PostMapping
    public String createTeacher(@RequestBody Teacher teacher) {
        teacherRepository.save(teacher);
        return "Teacher created successfully!";
    }

    @PutMapping("/{id}")
    public String updateTeacher(@PathVariable int id, @RequestBody Teacher updatedTeacher) {
        updatedTeacher = new Teacher(id, updatedTeacher.first_Name(), updatedTeacher.last_Name(), updatedTeacher.position(), updatedTeacher.department_id());
        teacherRepository.update(updatedTeacher);
        return "Teacher updated successfully!";
    }

    @DeleteMapping("/{id}")
    public String deleteTeacher(@PathVariable int id) {
        teacherRepository.deleteById(id);
        return "Teacher deleted successfully!";
    }
}
