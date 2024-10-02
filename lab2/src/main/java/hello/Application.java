package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

@SpringBootApplication
@Controller
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "calculator";
    }

    @PostMapping("/calculate")
    public String calculate(@RequestParam String expression, Model model) {
        try {
            double result = evaluateExpression(expression);
            model.addAttribute("result", result);
        } catch (Exception e) {
            model.addAttribute("error", "Помилка у виразі: " + e.getMessage());
        }
        return "calculator";
    }

    private double evaluateExpression(String expression) {
        Expression e = new ExpressionBuilder(expression).build();
        return e.evaluate();
    }
}