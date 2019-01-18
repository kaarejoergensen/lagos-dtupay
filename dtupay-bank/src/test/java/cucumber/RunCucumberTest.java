package cucumber;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)

@CucumberOptions(
        plugin = {"pretty"},
        features = "src/test/java/resources/cucumber"
)
public class RunCucumberTest {
}


