package com.mycompany.app;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class App {
    public static void main(String[] args) {
        String generatedPassword = getGeneratedPassword();
        System.out.println("Пароль: " + generatedPassword);
        System.out.println();

        Task2.getIPAddress();
        System.out.println();

        Task3.getWeatherForecast();
    }

    private static String getGeneratedPassword() {
        System.setProperty("webdriver.chrome.driver", "/Users/alexandr/Downloads/chromedriver-mac-arm64/chromedriver");
        WebDriver webDriver = new ChromeDriver();
        String password = "";

        try {
            webDriver.get("https://www.calculator.net/password-generator.html");
            return new WebDriverWait(webDriver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='verybigtext']")))
                    .getText().trim();

        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        } finally {
            webDriver.quit();
        }

        return password;
    }
}