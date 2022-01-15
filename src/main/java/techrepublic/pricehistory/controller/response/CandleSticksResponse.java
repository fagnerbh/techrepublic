package techrepublic.pricehistory.controller.response;

import java.util.Collection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @Getter @Setter
public class CandleSticksResponse {
	
	Collection<CandleStickDto> candlesticks;

}
