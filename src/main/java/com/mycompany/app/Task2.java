package com.mycompany.app;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Task2 {
    public static void getIPAddress() {
        WebDriverManager.chromedriver().setup();
        WebDriver webDriver = new ChromeDriver();

        try {
            String ipAddress = "Не удалось получить";

            try {
                webDriver.get("https://api.ipify.org/?format=json");
                WebElement preElement = webDriver.findElement(By.tagName("pre"));
                String jsonString = preElement.getText();
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
                ipAddress = (String) jsonObject.get("ip");
            } catch (Exception e1) {
                try {
                    webDriver.get("https://httpbin.org/ip");
                    WebElement preElement = webDriver.findElement(By.tagName("pre"));
                    String jsonString = preElement.getText();
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
                    ipAddress = (String) jsonObject.get("origin");
                    if (ipAddress.contains(",")) {
                        ipAddress = ipAddress.split(",")[0].trim();
                    }
                } catch (Exception e2) {
                    System.out.println("Не удалось подключиться к сервисам");
                }
            }

            System.out.println("Ваш IP-адрес: " + ipAddress);

        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        } finally {
            webDriver.quit();
        }
    }
}