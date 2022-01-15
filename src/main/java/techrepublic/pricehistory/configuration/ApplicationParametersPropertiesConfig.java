package techrepublic.pricehistory.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-parameters-${spring.profiles.active}.properties")
public class ApplicationParametersPropertiesConfig {

}
