package testScripts;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import base.TestBase;
import commonUtils.Utility;
import pages.CartPage;
import pages.HomePage;

public class TestFile extends TestBase{
	HomePage homePage;
	WebDriverWait wait;
	public static List<WebElement> ItemsInCart;
	CartPage cartpage;
	public int cartsize;
    
	
	@BeforeClass
	public void Reports() {
		ExtentSetup();
	}
	
	@BeforeTest
	public void launch() {
		initialize();
	}
	
	@Test(priority=1)
	public void LoginToApp() throws InterruptedException {
		extentTest=reports.createTest("Login Test");
		homePage=new HomePage();
		homePage.login();
		WebElement verifyName = homePage.username;
		wait = new WebDriverWait(driver,Duration.ofSeconds(30));
		wait.until(ExpectedConditions.textToBePresentInElement(verifyName, "Welcome Mkmani"));
		Assert.assertTrue(verifyName.isDisplayed());
		
		
	}
	@Test(priority=2,dataProvider = "ProductDetails")
	  public void additem(String category,String product) {
		extentTest=reports.createTest("Adding MultipleItem Test");
		homePage=new HomePage();
		homePage.selectItems(category, product);
	  }
	@DataProvider(name="ProductDetails")
	  public Object[][] getdata() throws CsvValidationException, IOException{
		  String path = System.getProperty("user.dir")+"\\src\\test\\resources\\testData\\MultipleItemData.csv";
		  CSVReader reader = new CSVReader(new FileReader(path));
		  String[] col;
		  ArrayList<Object> datalist = new ArrayList<Object>();
		  while((col = reader.readNext())!=null) {
			  Object[] record = {col[0],col[1]};
			  datalist.add(record);
		  }
		  	return datalist.toArray(new Object[datalist.size()][]);	  
	  }
	
	@Test(priority=3)
	  public void cart() throws InterruptedException {
		extentTest=reports.createTest("Cart page Test");
		homePage=new HomePage();
		homePage.cart();
		cartpage = new CartPage();
		cartpage.delete();
		  
	  }
	
	@Test(priority=4)
	  public void purchase() throws InterruptedException{
		extentTest=reports.createTest("Place Order Test");
		cartpage.orderDetails();
		WebElement msg = cartpage.message;
		Assert.assertTrue(msg.isDisplayed());
		cartpage.clickOk();
	  }

	@AfterMethod
	public void close(ITestResult result) {
		if(ITestResult.FAILURE==result.getStatus()) {
			  extentTest.log(Status.FAIL, result.getThrowable().getMessage());
			  String strpath=Utility.getScreenshotPath(driver);
			  extentTest.addScreenCaptureFromPath(strpath);
		}
	}
	
	@AfterTest
	public void close() {
		  driver.close();
		  reports.flush();
	  }
	
}
