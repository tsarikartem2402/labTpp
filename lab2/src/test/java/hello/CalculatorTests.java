package hello;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class CalculatorTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String extractResult(String body) {
        Pattern pattern = Pattern.compile("<span>(.*?)</span>");
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return body; // Повертаємо весь body, якщо не знайдено очікуваний формат
    }

    @Test
    public void testHomePage() throws Exception {
        ResponseEntity<String> entity = restTemplate
                .getForEntity("http://localhost:" + this.port + "/", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue(entity.getBody().contains("Калькулятор"), "Home page does not contain 'Калькулятор'");
        System.out.println("Home page test passed. Body contains 'Калькулятор'");
    }

    @Test
    public void testSimpleAddition() throws Exception {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("expression", "2 + 2");
        ResponseEntity<String> entity = restTemplate
                .postForEntity("http://localhost:" + this.port + "/calculate", formData, String.class);
        
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        String result = extractResult(entity.getBody());
        System.out.println("Simple addition result: " + result);
        assertEquals("4.0", result, "Simple addition failed");
    }

    @Test
    public void testComplexExpression() throws Exception {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("expression", "(10 + 5) * 2");
        ResponseEntity<String> entity = restTemplate
                .postForEntity("http://localhost:" + this.port + "/calculate", formData, String.class);
        
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        String result = extractResult(entity.getBody());
        System.out.println("Complex expression result: " + result);
        assertEquals("30.0", result, "Complex expression calculation failed");
    }

    @Test
    public void testInvalidExpression() throws Exception {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("expression", "2 + * 2");
        ResponseEntity<String> entity = restTemplate
                .postForEntity("http://localhost:" + this.port + "/calculate", formData, String.class);
        
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        String responseBody = entity.getBody();
        System.out.println("Invalid expression response: " + responseBody);
        
        boolean containsErrorMessage = responseBody != null && responseBody.contains("Помилка у виразі");
        assertTrue(containsErrorMessage, "Invalid expression not detected. Response: " + responseBody);
    }
}