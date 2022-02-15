import com.sun.glass.events.KeyEvent;
import io.github.bonigarcia.wdm.WebDriverManager;
import javafx.scene.input.KeyCode;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CircleProspecting {
    public static String EMAIL = "vidalit2016@gmail.com";
    public static String PASSWORD = "Skater123";
    public static Robot robot;

    public static void main(String args[]){
        File file = new File("C:\\Users\\caets\\Downloads\\myReport.xls");
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled", "start-maximized");

        WebDriver driver = new ChromeDriver(options);

        try {
            robot = new Robot();
            signIn(driver);
            Thread.sleep(3000);
            findContacts(driver, getOwners(file));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void signIn(WebDriver driver){
        driver.get("https://beenverified.com");

        driver.findElement(By.xpath("/html/body/header/nav/div[3]/div/ul/li[2]/a")).click();
        driver.findElement(By.xpath("//*[@id=\"login-email\"]")).sendKeys(EMAIL);
        driver.findElement(By.xpath("//*[@id=\"login-password\"]")).sendKeys(PASSWORD);
        driver.findElement(By.xpath("//*[@id=\"submit\"]")).click();
    }

    public static void findContacts(WebDriver driver, ArrayList<PropertyOwner> owners) throws InterruptedException {
        for (PropertyOwner owner : owners){
            driver.findElement(By.xpath("//*[@id=\"property-tab\"]")).click();
            driver.findElement(By.xpath("//*[@id=\"fullAddress\"]")).sendKeys(owner.getAddress() + " " + owner.getCitystatezip().split(" ")[0]); //+  " " + owner.getCitystatezip().split(" ")[1]);
            //driver.findElement(By.xpath("//*[@id=\"fullAddress\"]")).click();
            Thread.sleep(1000);
            robot.mouseMove(1079, 378);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            //System.out.println(MouseInfo.getPointerInfo().getLocation().toString());
            robot.keyPress(KeyEvent.VK_LEFT);
            Thread.sleep(1000);
            robot.keyPress(KeyEvent.VK_ENTER);
            Thread.sleep(8000);
            driver.get(driver.findElement(By.xpath("//*[@id=\"ember306\"]")).getAttribute("href"));
            Thread.sleep(8000);
            owner.setPhonenumber(driver.findElement(By.xpath("//*[@id=\"overview-section\"]/ul/li[2]/p")).getText());
            driver.findElement(By.id("ui-id-2")).click();
            //driver.findElement(By.xpath("//*[@id=\"property-search-btn-lg\"]")).click();
        }
    }

    public static ArrayList<PropertyOwner> getOwners(File file){

        ArrayList<PropertyOwner> propertyOwners = new ArrayList<PropertyOwner>();

        try
        {
            FileInputStream inputStream = new FileInputStream(file);

            Workbook workbook = new HSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = firstSheet.iterator();

            while (rowIterator.hasNext()){
                Row nextRow = rowIterator.next();

                PropertyOwner propertyOwner = new PropertyOwner();

                Iterator<Cell> cellIterator = nextRow.cellIterator();

                while (nextRow.getRowNum() > 0 && cellIterator.hasNext()){
                    Cell cell = cellIterator.next();

                    switch (cell.getCellType()) {
                        case STRING:

                            System.out.print(cell.getStringCellValue());
                            switch (cell.getColumnIndex()) {
                                case 0:
                                    propertyOwner.setParcelnumber(cell.getStringCellValue());
                                    break;
                                case 1:
                                    propertyOwner.setName(cell.getStringCellValue());
                                    break;
                                case 3:
                                    propertyOwner.setAddress(cell.getStringCellValue());
                                    break;
                                case 5:
                                    propertyOwner.setCitystatezip(cell.getStringCellValue());
                                    break;
                            }
                            break;
                        case BOOLEAN:

                            System.out.print(cell.getBooleanCellValue());
                            break;

                        case NUMERIC:

                            System.out.print(cell.getNumericCellValue());
                            break;

                        default:
                            break;
                    }
                    System.out.print(" - ");
                }
                System.out.println();
                if(propertyOwner.getParcelnumber() != null){
                    propertyOwners.add(propertyOwner);
                }
            }

            workbook.close();
            inputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return propertyOwners;
    }
}