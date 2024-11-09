package ttp.lab3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class SQLExecutorController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/query")
    public String executeQuery(@RequestParam("query") String query, Model model) {
        try {
            ParsedCommand parsedCommand = parseCommand(query.trim());
            if (parsedCommand == null) {
                model.addAttribute("error", "Неправильний формат запиту.");
                return "status";
            }

            String tableName = parsedCommand.getTableName();
            Map<String, Object> data = parsedCommand.getData();

            switch (parsedCommand.getCommandType()) {
                case "insert":
                    executeInsert(tableName, data, model);
                    break;
                case "update":
                    executeUpdate(tableName, data, model);
                    break;
                case "delete":
                    executeDelete(tableName, data, model);
                    break;
                case "read":
                    executeRead(tableName, model);
                    break;
                default:
                    model.addAttribute("error", "Непідтримуваний запит.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Помилка виконання запиту: " + e.getMessage());
        }
        return "status";
    }


    private ParsedCommand parseCommand(String query) {

        Pattern pattern = Pattern.compile("^(\\w+)\\s+(\\w+)\\(([^)]*)\\)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        if (!matcher.matches()) {
            System.out.println("Команда не відповідає очікуваному формату.");
            return null;
        }

        String commandType = matcher.group(1).toLowerCase();
        String tableName = matcher.group(2);
        String dataString = matcher.group(3);

        Map<String, Object> data = new LinkedHashMap<>();

        if (!commandType.equals("read") && !dataString.isEmpty()) {
            String[] pairs = dataString.split(",\\s*");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length != 2) {
                    System.out.println("Неправильний формат ключ-значення в парі: " + pair);
                    return null;
                }

                String key = keyValue[0].trim();
                String value = keyValue[1].trim().replace("'", "");


                Object finalValue;
                try {
                    finalValue = Integer.parseInt(value); 
                } catch (NumberFormatException e) {
                    finalValue = value;  
                }
                data.put(key, finalValue);
            }
        }

        System.out.println("Command Type: " + commandType);
        System.out.println("Table Name: " + tableName);
        System.out.println("Data: " + data);

        return new ParsedCommand(commandType, tableName, data);
    }

    private void executeInsert(String tableName, Map<String, Object> data, Model model) {
        try {
            if (tableName == null || tableName.isEmpty() || data == null || data.isEmpty()) {
                model.addAttribute("error", "Некоректні дані або ім'я таблиці.");
                return;
            }


            tableName = tableName.toLowerCase();
            
            StringBuilder columns = new StringBuilder();
            StringBuilder placeholders = new StringBuilder();
            List<Object> values = new ArrayList<>();

            System.out.println("Початкові дані: " + data);

            data.forEach((key, value) -> {
                if (!key.equalsIgnoreCase("id")) { 
 
                    String normalizedKey = key.toLowerCase();
                    columns.append(normalizedKey).append(", ");
                    placeholders.append("?, ");
                    values.add(value);
                    
     
                    System.out.println("Додаємо поле: " + normalizedKey + " = " + value);
                }
            });

            columns.setLength(columns.length() - 2);
            placeholders.setLength(placeholders.length() - 2);

            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, placeholders);
            System.out.println("Фінальний SQL запит: " + sql);
            System.out.println("Значення для підстановки: " + values);

            jdbcTemplate.update(sql, values.toArray());
            model.addAttribute("message", "Запис додано до таблиці " + tableName + ".");
        } catch (Exception e) {
            System.out.println("Помилка при виконанні INSERT: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Помилка виконання запиту: " + e.getMessage());
        }
    }

    // Метод для UPDATE-запиту
    private void executeUpdate(String tableName, Map<String, Object> data, Model model) {
        if (!data.containsKey("id")) {
            model.addAttribute("error", "ID є обов'язковим для оновлення запису.");
            return;
        }

        StringBuilder setClause = new StringBuilder();
        List<Object> values = new ArrayList<>();

        data.forEach((key, value) -> {
            if (!key.equals("id")) {
                setClause.append(key).append(" = ?, ");
                values.add(value);
            }
        });

        setClause.setLength(setClause.length() - 2);
        values.add(Integer.parseInt(data.get("id").toString()));

        String sql = String.format("UPDATE %s SET %s WHERE id = ?", tableName, setClause);
        jdbcTemplate.update(sql, values.toArray());
        model.addAttribute("message", "Запис оновлено.");
    }

    // Метод для DELETE-запиту
    private void executeDelete(String tableName, Map<String, Object> data, Model model) {
        if (!data.containsKey("id")) {
            model.addAttribute("error", "ID є обов'язковим для видалення запису.");
            return;
        }

        String sql = String.format("DELETE FROM %s WHERE id = ?", tableName);
        jdbcTemplate.update(sql, Integer.parseInt(data.get("id").toString()));
        model.addAttribute("message", "Запис видалено.");
    }

    // Метод для SELECT-запиту
    private void executeRead(String tableName, Model model) {
        String sql = String.format("SELECT * FROM %s", tableName);
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        model.addAttribute("result", result);
        model.addAttribute("message", "Запит виконано.");
    }


    private static class ParsedCommand {
        private final String commandType;
        private final String tableName;
        private final Map<String, Object> data;

        public ParsedCommand(String commandType, String tableName, Map<String, Object> data) {
            this.commandType = commandType;
            this.tableName = tableName;
            this.data = data;
        }

        public String getCommandType() {
            return commandType;
        }

        public String getTableName() {
            return tableName;
        }

        public Map<String, Object> getData() {
            return data;
        }
    }
}
