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
import java.util.ArrayList;
import java.util.List;

public class Task3 {

    private static final double LATITUDE = 56.0;
    private static final double LONGITUDE = 44.0;
    private static final String WEATHER_API_URL = "https://api.open-meteo.com/v1/forecast?latitude=" + LATITUDE + "&longitude=" + LONGITUDE +
            "&hourly=temperature_2m,rain" +
            "&current=cloud_cover" +
            "&timezone=Europe%2FMoscow" +
            "&forecast_days=1" +
            "&wind_speed_unit=ms";

    private static final DateTimeFormatter SOURCE_DATE_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter TARGET_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void getWeatherForecast() {
        WebDriverManager.chromedriver().setup();
        WebDriver webController = new ChromeDriver();

        try {
            webController.get(WEATHER_API_URL);
            WebElement jsonContainer = webController.findElement(By.tagName("pre"));
            String serverResponse = jsonContainer.getText();

            WeatherDataHolder weatherInfo = parseWeatherData(serverResponse);

            displayWeatherTable(weatherInfo);

            saveForecastToFile(weatherInfo, "result/forecast.txt");

            // Дополнительный функционал: вывод статистики
            displayWeatherStatistics(weatherInfo);

        } catch (Exception processingError) {
            System.err.println("Ошибка при получении прогноза: " + processingError.getMessage());
            processingError.printStackTrace();
        } finally {
            if (webController != null) {
                webController.quit();
            }
        }
    }

    private static WeatherDataHolder parseWeatherData(String jsonResponse) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject rootObject = (JSONObject) parser.parse(jsonResponse);
        JSONObject hourlyData = (JSONObject) rootObject.get("hourly");

        JSONArray timeSlots = (JSONArray) hourlyData.get("time");
        JSONArray tempValues = (JSONArray) hourlyData.get("temperature_2m");
        JSONArray rainValues = (JSONArray) hourlyData.get("rain");

        List<String> formattedTimes = new ArrayList<>();
        List<Double> temperatures = new ArrayList<>();
        List<Double> rainfalls = new ArrayList<>();

        for (int idx = 0; idx < timeSlots.size(); idx++) {
            String rawTime = (String) timeSlots.get(idx);
            LocalDateTime parsedDateTime = LocalDateTime.parse(rawTime, SOURCE_DATE_FORMAT);
            formattedTimes.add(parsedDateTime.format(TARGET_DATE_FORMAT));

            temperatures.add(((Number) tempValues.get(idx)).doubleValue());
            rainfalls.add(((Number) rainValues.get(idx)).doubleValue());
        }

        return new WeatherDataHolder(formattedTimes, temperatures, rainfalls);
    }

    private static void displayWeatherTable(WeatherDataHolder data) {
        String tableHeader = String.format("%-5s %-20s %-12s %-12s", "№", "Дата/время", "Температура", "Осадки (мм)");
        String separator = "--------------------------------------------------------";

        System.out.println(tableHeader);
        System.out.println(separator);

        for (int idx = 0; idx < data.times.size(); idx++) {
            String row = String.format("%-5d %-20s %-12.1f %-12.1f",
                    (idx + 1),
                    data.times.get(idx),
                    data.temperatures.get(idx),
                    data.rainfalls.get(idx));
            System.out.println(row);
        }
    }

    private static void saveForecastToFile(WeatherDataHolder data, String filePath) throws Exception {
        java.io.File outputDirectory = new java.io.File("result");
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        try (PrintWriter fileWriter = new PrintWriter(new FileWriter(filePath))) {
            fileWriter.println(String.format("%-5s %-20s %-12s %-12s", "№", "Дата/время", "Температура", "Осадки (мм)"));
            fileWriter.println("--------------------------------------------------------");

            for (int idx = 0; idx < data.times.size(); idx++) {
                fileWriter.println(String.format("%-5d %-20s %-12.1f %-12.1f",
                        (idx + 1),
                        data.times.get(idx),
                        data.temperatures.get(idx),
                        data.rainfalls.get(idx)));
            }
        }

        System.out.println("\n[СОХРАНЕНО] Прогноз записан в: " + filePath);
    }

    // Дополнительный функционал: статистика по температуре и осадкам
    private static void displayWeatherStatistics(WeatherDataHolder data) {
        double avgTemp = data.temperatures.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double maxTemp = data.temperatures.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double minTemp = data.temperatures.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);

        double totalRain = data.rainfalls.stream().mapToDouble(Double::doubleValue).sum();
        long rainyHours = data.rainfalls.stream().filter(rain -> rain > 0.0).count();

        System.out.println("\n--- Статистика погоды на сутки ---");
        System.out.printf("Средняя температура: %.1f°C%n", avgTemp);
        System.out.printf("Максимальная температура: %.1f°C%n", maxTemp);
        System.out.printf("Минимальная температура: %.1f°C%n", minTemp);
        System.out.printf("Общее количество осадков: %.1f мм%n", totalRain);
        System.out.printf("Часов с осадками: %d из %d%n", rainyHours, data.times.size());
        System.out.println("----------------------------------\n");
    }

    // Вспомогательный класс-контейнер для данных
    private static class WeatherDataHolder {
        List<String> times;
        List<Double> temperatures;
        List<Double> rainfalls;

        WeatherDataHolder(List<String> times, List<Double> temperatures, List<Double> rainfalls) {
            this.times = times;
            this.temperatures = temperatures;
            this.rainfalls = rainfalls;
        }
    }
}