package com.mycompany.app;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Task2 {

    private static final String FIRST_IP_SERVICE = "https://api.ipify.org/?format=json";
    private static final String SECOND_IP_SERVICE = "https://httpbin.org/ip";

    public static void getIPAddress() {
        WebDriverManager.chromedriver().setup();
        WebDriver browserNavigator = new ChromeDriver();

        String detectedIp = "Не определено";

        try {
            detectedIp = tryFetchIpFromService(browserNavigator, FIRST_IP_SERVICE, "ip");

            if ("Не определено".equals(detectedIp)) {
                detectedIp = tryFetchIpFromService(browserNavigator, SECOND_IP_SERVICE, "origin");

                // Обработка случая с несколькими IP через запятую
                if (detectedIp != null && detectedIp.contains(",")) {
                    detectedIp = detectedIp.split(",")[0].trim();
                }
            }

            if (detectedIp == null || "Не определено".equals(detectedIp)) {
                detectedIp = "Сервисы недоступны";
            }

        } catch (Exception globalError) {
            System.err.println("Критическая ошибка: " + globalError.getMessage());
        } finally {
            if (browserNavigator != null) {
                browserNavigator.quit();
            }
        }

        System.out.println("Внешний IP-адрес: " + detectedIp);
    }

    private static String tryFetchIpFromService(WebDriver driver, String serviceUrl, String jsonKey) {
        try {
            driver.get(serviceUrl);
            WebElement rawJsonElement = driver.findElement(By.tagName("pre"));
            String jsonContent = rawJsonElement.getText();

            JSONParser jsonDecoder = new JSONParser();
            JSONObject parsedData = (JSONObject) jsonDecoder.parse(jsonContent);

            Object ipValue = parsedData.get(jsonKey);
            return ipValue != null ? ipValue.toString() : null;

        } catch (Exception fetchError) {
            System.err.println("Не удалось получить данные от " + serviceUrl + ": " + fetchError.getMessage());
            return null;
        }
    }
}