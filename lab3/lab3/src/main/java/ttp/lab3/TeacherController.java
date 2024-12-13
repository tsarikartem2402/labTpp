package ttp.lab3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;


    @GetMapping
    public String showTeacherPage(Model model) {
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "teachers"; 
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public String saveTeacher(@ModelAttribute Teacher teacher, Model model) {
        String result = teacherService.saveTeacher(teacher);
        model.addAttribute("message", result);
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "teachers";
    }


    @PostMapping("/search")
    public String searchTeacher(@RequestParam("searchId") int id, Model model) {
        Teacher teacher = teacherService.findTeacherById(id);
        if (teacher != null) {
            model.addAttribute("teacher", teacher);
            model.addAttribute("message", "Викладача знайдено!");
        } else {
            model.addAttribute("error", "Викладача з ID " + id + " не знайдено.");
        }
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "teachers";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public String deleteTeacher(@RequestParam("deleteId") int id, Model model) {
        String result = teacherService.deleteTeacher(id);
        if (result.contains("успішно")) {
            model.addAttribute("message", result);
        } else {
            model.addAttribute("error", result);
        }
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "teachers";
    }


    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        model.addAttribute("error", "Виникла помилка: " + e.getMessage());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "teachers";
    }
}