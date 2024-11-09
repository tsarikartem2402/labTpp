package ttp.lab3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@Controller
public class Lab3Application {

    @GetMapping("/")
    public String home() {
        return "status";
    }

    public static void main(String[] args) {
        SpringApplication.run(Lab3Application.class, args);
    }
}
