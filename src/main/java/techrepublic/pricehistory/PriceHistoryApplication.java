package techrepublic.pricehistory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PriceHistoryApplication {	
	
	public static void main(String[] args) {
		SpringApplication.run(PriceHistoryApplication.class, args);	
	}

}
