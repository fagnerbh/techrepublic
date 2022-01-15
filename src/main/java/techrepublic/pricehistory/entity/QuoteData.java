package techrepublic.pricehistory.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class QuoteData {
	
	private String isin;
	private double price;
}
