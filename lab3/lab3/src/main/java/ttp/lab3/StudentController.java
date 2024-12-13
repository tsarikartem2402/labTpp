package ttp.lab3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;


    @GetMapping
    public String showStudentPage(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students"; 
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public String saveStudent(@ModelAttribute Student student, Model model) {
        String result = studentService.saveStudent(student);
        model.addAttribute("message", result);
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }


    @PostMapping("/search")
    public String searchStudent(@RequestParam("searchId") int id, Model model) {
        Student student = studentService.findStudentById(id);
        if (student != null) {
            model.addAttribute("student", student);
            model.addAttribute("message", "Студента знайдено!");
        } else {
            model.addAttribute("error", "Студента з ID " + id + " не знайдено.");
        }
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public String deleteStudent(@RequestParam("deleteId") int id, Model model) {
        String result = studentService.deleteStudent(id);
        if (result.contains("успішно")) {
            model.addAttribute("message", result);
        } else {
            model.addAttribute("error", result);
        }
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }


    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        model.addAttribute("error", "Виникла помилка: " + e.getMessage());
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }
}