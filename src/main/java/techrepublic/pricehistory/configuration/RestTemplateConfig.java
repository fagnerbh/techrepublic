package techrepublic.pricehistory.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate configuration only for tests
 * @author fagner
 *
 */
@Configuration
public class RestTemplateConfig {	
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {		
		return builder.build();
	}

}
