package techrepublic.pricehistory.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @Getter @Setter
public class CandleStickDto {

	private String openTimestamp;
	private Double openPrice;
	private Double highPrice;
	private Double lowPrice;
	private Double closePrice;
	private String closeTimestamp;
	private String isin;
	private Boolean closed;
	
}
