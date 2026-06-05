package com.mycompany.app;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class App {

    private static final String PASSWORD_GENERATOR_URL = "https://www.calculator.net/password-generator.html";
    private static final int TIMEOUT_SECONDS = 10;

    public static void main(String[] args) {
        displayResults();
    }

    private static void displayResults() {
        String secureKey = extractSecurePassword();
        System.out.println("Сгенерированный ключ: " + secureKey);
        System.out.println();

        Task2.getIPAddress();
        System.out.println();

        Task3.getWeatherForecast();
    }

    private static String extractSecurePassword() {
        WebDriverManager.chromedriver().setup();
        WebDriver browserDriver = new ChromeDriver();

        try {
            browserDriver.get(PASSWORD_GENERATOR_URL);
            WebDriverWait waiter = new WebDriverWait(browserDriver, TIMEOUT_SECONDS);

            String generatedCode = waiter.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='verybigtext']"))
            ).getText().trim();

            return generatedCode.replaceAll("^\\s+", "").replaceAll("\\s+$", "");

        } catch (Exception executionError) {
            System.err.println("Ошибка при получении пароля: " + executionError.getMessage());
            return "Ошибка_генерации";
        } finally {
            if (browserDriver != null) {
                browserDriver.quit();
            }
        }
    }
}
