package ttp.lab3;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public String showSubjectPage(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "subjects";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public String saveSubject(@ModelAttribute Subject subject, Model model) {
        String result = subjectService.saveSubject(subject);
        model.addAttribute("message", result);
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "subjects";
    }

    @PostMapping("/search")
    public String searchSubject(@RequestParam("searchId") int id, Model model) {
        Subject subject = subjectService.findSubjectById(id);
        if (subject != null) {
            model.addAttribute("subject", subject);
            model.addAttribute("message", "Subject found!");
        } else {
            model.addAttribute("error", "Subject with ID " + id + " not found.");
        }
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "subjects";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public String deleteSubject(@RequestParam("deleteId") int id, Model model) {
        String result = subjectService.deleteSubject(id);
        if (result.contains("successfully")) {
            model.addAttribute("message", result);
        } else {
            model.addAttribute("error", result);
        }
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "subjects";
    }

    @PostMapping("/department")
    public String findByDepartment(@RequestParam("departmentId") int departmentId, Model model) {
        List<Subject> subjects = subjectService.findSubjectsByDepartment(departmentId);
        if (!subjects.isEmpty()) {
            model.addAttribute("subjects", subjects);
            model.addAttribute("message", "Found " + subjects.size() + " subjects in department " + departmentId);
        } else {
            model.addAttribute("error", "No subjects found in department " + departmentId);
            model.addAttribute("subjects", subjectService.getAllSubjects());
        }
        return "subjects";
    }
}