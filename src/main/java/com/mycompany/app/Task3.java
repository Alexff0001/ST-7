package com.mycompany.app;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task3 {
    public static void getWeatherForecast() {
        WebDriverManager.chromedriver().setup();
        WebDriver webDriver = new ChromeDriver();

        try {
            String url = "https://api.open-meteo.com/v1/forecast?latitude=56&longitude=44" +
                    "&hourly=temperature_2m,rain" +
                    "&current=cloud_cover" +
                    "&timezone=Europe%2FMoscow" +
                    "&forecast_days=1" +
                    "&wind_speed_unit=ms";

            webDriver.get(url);

            WebElement preElement = webDriver.findElement(By.tagName("pre"));
            String jsonString = preElement.getText();

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

            JSONObject hourly = (JSONObject) jsonObject.get("hourly");
            JSONArray times = (JSONArray) hourly.get("time");
            JSONArray temperatures = (JSONArray) hourly.get("temperature_2m");
            JSONArray rains = (JSONArray) hourly.get("rain");

            StringBuilder output = new StringBuilder();
            String header = String.format("%-5s %-20s %-12s %-12s%n", "№", "Дата/время", "Температура", "Осадки (мм)");
            String separator = "--------------------------------------------------------\n";

            output.append(header);
            output.append(separator);

            System.out.print(header);
            System.out.print(separator);

            DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (int i = 0; i < times.size(); i++) {
                String timeStr = (String) times.get(i);
                LocalDateTime dateTime = LocalDateTime.parse(timeStr, inputFormatter);
                String formattedTime = dateTime.format(outputFormatter);

                double temperature = ((Number) temperatures.get(i)).doubleValue();
                double rain = ((Number) rains.get(i)).doubleValue();

                String line = String.format("%-5d %-20s %-12.1f %-12.1f%n",
                        (i + 1), formattedTime, temperature, rain);

                output.append(line);
                System.out.print(line);
            }

            java.io.File resultDir = new java.io.File("result");
            if (!resultDir.exists()) {
                resultDir.mkdirs();
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter("result/forecast.txt"))) {
                writer.print(output.toString());
                System.out.println("\nПрогноз сохранен в файл: result/forecast.txt");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
            e.printStackTrace();
        } finally {
            webDriver.quit();
        }
    }
}