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


public class MyTest {
    private String targetUrl;
    private String fileresult;

    @Before
    public void readSettings() {
        String address = "D:/Projects/url.json";

        try {
            FileReader reader = new FileReader(address);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            String url = (String) jsonObject.get("url");
            String file = (String) jsonObject.get("file");
            this.fileresult = file;
            this.targetUrl = url;

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Test

    public void currencyValue() throws InterruptedException, IOException {
        //TIMER
        int pauseInMilliSeconds = 1000;
        //ELEMENTS XPATH
        String staticButtonXpath = "//*[@id=\"mm-2\"]/ul/ul/li[9]/a";
        String currencyValue = "//*[@id=\"mm-52\"]/ul/li[1]/a";
        String everyDayCurrencyValue = "//*[@id=\"mm-53\"]/ul/li[1]/a";
        String chooseDate = "//*[@id=\"form0\"]/aside[2]/div/div/div/div/div[3]/div[2]/div/div[2]/span[7]";
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
        Thread.sleep(pauseInMilliSeconds);

        WebElement stat = driver.findElement(By.xpath(staticButtonXpath));
        stat.click();
        Thread.sleep(pauseInMilliSeconds);

        WebElement rate = driver.findElement(By.xpath(currencyValue));
        rate.click();
        Thread.sleep(pauseInMilliSeconds);

        WebElement BYN = driver.findElement(By.xpath(everyDayCurrencyValue));
        BYN.click();
        Thread.sleep(pauseInMilliSeconds);

        WebElement date = driver.findElement(By.xpath(chooseDate));
        date.click();
        Thread.sleep(pauseInMilliSeconds);

        WebElement dollar = driver.findElement(By.xpath(dollarXpath));
        WebElement valueD = driver.findElement(By.xpath(dollarValueXpath));
        WebElement euro = driver.findElement(By.xpath(euroXpath));
        WebElement valueE = driver.findElement(By.xpath(euroValueXpath));
        String par = dollar.getText();
        String par2 = valueD.getText();
        String tw = euro.getText();
        String tw2 = valueE.getText();
        System.out.println(par + "=" + par2 + ";" + " " + tw + "=" + tw2);


        File file;
        file = new File(fileresult);
        FileWriter fw = new FileWriter(file);

        BufferedWriter writer = new BufferedWriter(fw);
        writer.write(par + "=" + par2 + ";" + " " + tw + "=" + tw2);
        writer.close();

        driver.quit();
    }
}
