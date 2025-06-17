
package dreamportal;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;
import java.util.Set;

public class DreamPortalTests {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test(priority = 1)
    public void testHomePage() throws InterruptedException {
        driver.get("https://arjitnigam.github.io/myDreams/");

        WebElement loader = driver.findElement(By.className("loader"));
        Assert.assertTrue(loader.isDisplayed(), "Loader should be visible");

        Thread.sleep(3500); // Wait for loader to disappear

        WebElement mainContent = driver.findElement(By.cssSelector("h1"));
        Assert.assertTrue(mainContent.isDisplayed(), "Main content should be visible");

        WebElement myDreamsButton = driver.findElement(By.xpath("//button[text()='My Dreams']"));
        Assert.assertTrue(myDreamsButton.isDisplayed(), "My Dreams button should be visible");

        String mainWindow = driver.getWindowHandle();
        myDreamsButton.click();
        Thread.sleep(2000);

        Set<String> windows = driver.getWindowHandles();
        Assert.assertEquals(windows.size(), 3, "Should open 2 new tabs");

        for (String win : windows) {
            if (!win.equals(mainWindow)) {
                driver.switchTo().window(win);
                String url = driver.getCurrentUrl();
                Assert.assertTrue(url.contains("dreams-diary") || url.contains("dreams-total"),
                        "Opened URL should be diary or total page");
            }
        }

        driver.switchTo().window(mainWindow);
    }

    @Test(priority = 2)
    public void testDreamsDiary() {
        driver.get("https://arjitnigam.github.io/myDreams/dreams-diary.html");

        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        Assert.assertEquals(rows.size(), 10, "Should be 10 dream entries");

        for (WebElement row : rows) {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            Assert.assertEquals(cols.size(), 3, "Each row must have 3 columns");

            String dreamName = cols.get(0).getText().trim();
            String daysAgo = cols.get(1).getText().trim();
            String dreamType = cols.get(2).getText().trim();

            Assert.assertFalse(dreamName.isEmpty(), "Dream Name should not be empty");
            Assert.assertFalse(daysAgo.isEmpty(), "Days Ago should not be empty");
            Assert.assertTrue(dreamType.equals("Good") || dreamType.equals("Bad"), "Dream Type must be Good or Bad");
        }
    }

    @Test(priority = 3)
    public void testDreamsTotal() {
        driver.get("https://arjitnigam.github.io/myDreams/dreams-total.html");

        String body = driver.findElement(By.tagName("body")).getText();

        Assert.assertTrue(body.contains("Good Dreams: 6"), "Check Good Dreams count");
        Assert.assertTrue(body.contains("Bad Dreams: 4"), "Check Bad Dreams count");
        Assert.assertTrue(body.contains("Total Dreams: 10"), "Check Total Dreams count");
        Assert.assertTrue(body.contains("Recurring Dreams: 2"), "Check Recurring Dreams count");
        Assert.assertTrue(body.contains("Flying over mountains"), "Check recurring dream: Flying over mountains");
        Assert.assertTrue(body.contains("Lost in maze"), "Check recurring dream: Lost in maze");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
