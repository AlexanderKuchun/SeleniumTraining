package by.nbrb;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SecondTest {
    private String targetUrl;
    private String result;
    private String expectedUSD;
    private String expectedEUR;
    private String usdFromDatabase;
    private String eurFromDatabase;
    static String ADDRESS = "D:/Projects/url.json";
    private static final int PAUSEINMILLISECONDS = 1000;

    @Before
    public void readSettings() throws SQLException {


        try {
            FileReader reader = new FileReader(ADDRESS);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            this.targetUrl = (String) jsonObject.get("url");
            this.result = (String) jsonObject.get("file");
            this.expectedUSD = (String) jsonObject.get("1 USD");
            this.expectedEUR = (String) jsonObject.get("1 EUR");

        } catch (ParseException | IOException | NullPointerException ex) {
            ex.printStackTrace();
            System.exit(1);
        }


        String username = "postgres";
        String password = "Vasilchuk1";
        String url = "jdbc:postgresql://localhost:5432/postgres";

        Connection connection = null;
        ResultSet resultSet = null;
        ResultSet resultSet2 = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM public.rates WHERE id = (?)");

            statement.setInt(1, 1);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                this.usdFromDatabase = resultSet.getString("rate");
            }
            statement.setInt(1, 2);
            resultSet2 = statement.executeQuery();
            if (resultSet2.next()) {
                this.eurFromDatabase = resultSet2.getString("rate");
            }
        } finally {
            if (connection != null)
                connection.close();
            if (resultSet != null)
                resultSet.close();
            if (resultSet2 != null)
                resultSet2.close();
        }
    }

    @Test

    public void currencyValue() throws InterruptedException, IOException {
        //ELEMENTS XPATH
        String staticButtonXpath = "//*[@id=\"mm-2\"]/ul/ul/li[9]/a";
        String currencyValue = "//*[@id=\"mm-52\"]/ul/li[1]/a";
        String everyDayCurrencyValue = "//*[@id=\"mm-53\"]/ul/li[1]/a";
        String chooseDate = "//*[@id=\"form0\"]/aside[2]/div/div/div/div/div[3]/div[2]/div/div[2]/span[3]";
        // TABLE
        String currencyTableXpath = "//*[@id=\"ratesData\"]/table/tbody/";
        String dollarXpath = currencyTableXpath + "tr[6]/td[2]";
        String dollarValueXpath = currencyTableXpath + "tr[6]/td[3]/div";
        String euroXpath = currencyTableXpath + "tr[7]/td[2]";
        String euroValueXpath = currencyTableXpath + "tr[7]/td[3]/div";

        System.setProperty("webdriver.chrome.driver", "/chromedriver/chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();

        driver.manage().window().maximize();
        driver.get(targetUrl);
        Thread.sleep(PAUSEINMILLISECONDS);

        WebElement stat = driver.findElement(By.xpath(staticButtonXpath));
        stat.click();
        Thread.sleep(PAUSEINMILLISECONDS);

        WebElement rate = driver.findElement(By.xpath(currencyValue));
        rate.click();
        Thread.sleep(PAUSEINMILLISECONDS);

        WebElement BYN = driver.findElement(By.xpath(everyDayCurrencyValue));
        BYN.click();
        Thread.sleep(PAUSEINMILLISECONDS);

        WebElement date = driver.findElement(By.xpath(chooseDate));
        date.click();
        Thread.sleep(PAUSEINMILLISECONDS);

        WebElement dollar = driver.findElement(By.xpath(dollarXpath));
        WebElement valueD = driver.findElement(By.xpath(dollarValueXpath));
        WebElement euro = driver.findElement(By.xpath(euroXpath));
        WebElement valueE = driver.findElement(By.xpath(euroValueXpath));
        String usd = dollar.getText();
        String usdPrice = valueD.getText();
        String eur = euro.getText();
        String eurPrice = valueE.getText();
        System.out.println(usd + "=" + usdPrice + ";" + " " + eur + "=" + eurPrice);


        File file;
        file = new File(result);
        FileWriter fw = new FileWriter(file);

        BufferedWriter writer = new BufferedWriter(fw);
        writer.write(usd + "=" + usdPrice + ";" + " " + eur + "=" + eurPrice);
        writer.close();

        driver.quit();

        assertEquals(expectedUSD, usdPrice);
        assertEquals(expectedEUR, eurPrice);
        assertEquals(usdFromDatabase, usdPrice);
        assertEquals(eurFromDatabase, eurPrice);

    }
}
