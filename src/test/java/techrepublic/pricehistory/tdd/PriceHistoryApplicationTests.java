package techrepublic.pricehistory.tdd;

import org.junit.runner.RunWith;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableScheduling
@TestPropertySource(locations = {
		"classpath:application-test.properties",
		"classpath:application-parameters-test.properties"
})
class PriceHistoryApplicationTests {		            

	public static void main(String[] args) {
	       new SpringApplicationBuilder(PriceHistoryApplicationTests.class)
	               .web(WebApplicationType.NONE)
	               .run(args);
	   }

}
