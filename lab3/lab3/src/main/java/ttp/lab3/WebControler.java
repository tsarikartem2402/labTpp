package ttp.lab3;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebControler {


    @GetMapping("/")
    public String main() {
        return "home";
    }
    @GetMapping("/students/add")
    public String addStudent() {
        return "students";
    }

    @GetMapping("/teachers/add")
    public String addTeacher() {
        return "teachers";
    }

    @GetMapping("/subjects/add")
    public String addSubject() {
        return "subjects";
    }

    @GetMapping("/departments/add")
    public String addDepartment() {
        return "departments";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
    @GetMapping("/login")
    public String login() {
    	return "login";
    }
}
