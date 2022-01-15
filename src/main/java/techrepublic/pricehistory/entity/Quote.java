package techrepublic.pricehistory.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Quote {
		
	private QuoteData data;
	private String type;
	
}
