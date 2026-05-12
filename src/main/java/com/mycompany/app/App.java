package com.mycompany.app;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
        WebDriverManager.chromedriver().setup();
        WebDriver webDriver = new ChromeDriver();

        try {
            webDriver.get("https://www.calculator.net/password-generator.html");
            WebDriverWait wait = new WebDriverWait(webDriver, 10);
            return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='verybigtext']")))
                    .getText().trim();

        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        } finally {
            webDriver.quit();
        }
        return "";
    }
}