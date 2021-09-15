package by.nbrb;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;
import java.sql.*;


public class SecondTest {
    private String targetUrl;
    private String fileresult;
    private String expectedUSD;
    private String expectedEUR;
    private String usdFromDatabase;
    private String eurFromDatabase;
    static String ADDRESS = "D:/Projects/url.json";
    static int PauseInMilliSeconds = 1000;

    @Before
    public void readSettings() throws SQLException {

        try {
            FileReader reader = new FileReader(ADDRESS);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            String url = (String) jsonObject.get("url");
            String file = (String) jsonObject.get("file");
            String usd = (String) jsonObject.get("1 USD");
            String eur = (String) jsonObject.get("1 EUR");

            this.fileresult = file;
            this.targetUrl = url;
            this.expectedUSD = usd;
            this.expectedEUR = eur;

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        String username = "postgres";
        String password = "Vasilchuk1";
        String url = "jdbc:postgresql://localhost:5432/postgres";

        Connection connection = DriverManager.getConnection(url, username, password);

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM public.rates WHERE id = (?)")) {

            statement.setInt(1, 1);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String usdRateInTable = resultSet.getString("rate");
                this.usdFromDatabase = usdRateInTable;
            }
            statement.setInt(1, 2);
            ResultSet resultSet2 = statement.executeQuery();
            if (resultSet2.next()) {
                String eurRateInTable = resultSet2.getString("rate");
                this.eurFromDatabase = eurRateInTable;
            }
        } finally {
            connection.close();
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
        Thread.sleep(PauseInMilliSeconds);

        WebElement stat = driver.findElement(By.xpath(staticButtonXpath));
        stat.click();
        Thread.sleep(PauseInMilliSeconds);

        WebElement rate = driver.findElement(By.xpath(currencyValue));
        rate.click();
        Thread.sleep(PauseInMilliSeconds);

        WebElement BYN = driver.findElement(By.xpath(everyDayCurrencyValue));
        BYN.click();
        Thread.sleep(PauseInMilliSeconds);

        WebElement date = driver.findElement(By.xpath(chooseDate));
        date.click();
        Thread.sleep(PauseInMilliSeconds);

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
        file = new File(fileresult);
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
