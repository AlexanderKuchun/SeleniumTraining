package by.nbrb;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


import java.lang.String;

import java.io.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SecondTest {

    private String targetUrl;
    private String dataFromTheSite;
    private String usdFromFile;
    private String eurFromFile;
    private String usdFromDatabase;
    private String eurFromDatabase;
    static String pathToTheFile = "D:/Projects/Folder/file1.json";

    private static final int PAUSEINMILLISECONDS = 1000;

    @Before
    public void readSettings() throws SQLException {


        try {
            FileReader reader = new FileReader(pathToTheFile);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            this.targetUrl = (String) jsonObject.get("url");
            this.dataFromTheSite = (String) jsonObject.get("file");
            this.usdFromFile = (String) jsonObject.get("1 USD");
            this.eurFromFile = (String) jsonObject.get("1 EUR");

        } catch (ParseException | IOException | NullPointerException ex) {
            ex.printStackTrace();
            System.exit(1);
        }


        String USERNAME = "postgres";
        String PASSWORD = "Vasilchuk1";
        String URL = "jdbc:postgresql://localhost:5432/postgres";

        Connection connection = null;
        ResultSet resultSet = null;
        ResultSet resultSet2 = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
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
        String chooseDate = "//*[@id=\"form0\"]/aside[2]/div/div/div/div/div[3]/div[2]/div/div[2]/span[1]";
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
        String usdPriceActual = valueD.getText();
        String eur = euro.getText();
        String eurPriceActual = valueE.getText();
        System.out.println(usd + "=" + usdPriceActual + ";" + " " + eur + "=" + eurPriceActual);


        File file;
        file = new File(dataFromTheSite);
        FileWriter fw = new FileWriter(file);

        BufferedWriter writer = new BufferedWriter(fw);
        writer.write(usd + "=" + usdPriceActual + ";" + " " + eur + "=" + eurPriceActual);
        writer.close();

        driver.quit();

        System.out.println(usd + "=" + usdFromFile + ";" + " " + eur + "=" + eurFromFile);
        System.out.println(usd + "=" + usdFromDatabase + ";" + " " + eur + "=" + eurFromDatabase);

        assertEquals(usdFromFile, usdPriceActual);
        assertEquals(eurFromFile, eurPriceActual);
        assertEquals(usdFromDatabase, usdPriceActual);
        assertEquals(eurFromDatabase, eurPriceActual);

    }
}

