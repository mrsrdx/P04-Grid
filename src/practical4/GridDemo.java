package practical4;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class GridDemo {
    public static void main(String[] args) throws Exception {
        String grid = System.getProperty("gridUrl", "http://localhost:4444");
        String page = "<title>Grid Demo</title><h1>Remote WebDriver works</h1>";
        String url = "data:text/html," + URLEncoder.encode(page, StandardCharsets.UTF_8).replace("+", "%20");
        int failures = 0;
        for (String browser : new String[] {"chrome", "edge"}) {
            WebDriver driver = null;
            long start = System.nanoTime();
            try {
                MutableCapabilities options;
                if (browser.equals("chrome")) {
                    ChromeOptions chrome = new ChromeOptions();
                    if (Boolean.getBoolean("headless")) chrome.addArguments("--headless=new");
                    options = chrome;
                } else {
                    EdgeOptions edge = new EdgeOptions();
                    if (Boolean.getBoolean("headless")) edge.addArguments("--headless=new");
                    options = edge;
                }
                driver = new RemoteWebDriver(URI.create(grid).toURL(), options);
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
                driver.get(url);
                if (!driver.getTitle().equals("Grid Demo")) throw new IllegalStateException("Wrong title");
                System.out.printf("%s: PASS | title=%s | %d ms%n", browser, driver.getTitle(),
                        (System.nanoTime() - start) / 1_000_000);
            } catch (Exception e) {
                failures++;
                System.err.println(browser + ": FAIL | " + e.getMessage());
            } finally {
                if (driver != null) driver.quit();
            }
        }
        if (failures > 0) throw new IllegalStateException(failures + " browser test(s) failed");
        System.out.println("Cross-browser comparison: both browsers passed the same check.");
    }
}
