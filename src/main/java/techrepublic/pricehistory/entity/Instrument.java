package techrepublic.pricehistory.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * class representing the partner's instrument endpoint payload 
 * @author fagner
 *
 */
@Getter @Setter @NoArgsConstructor
public class Instrument {
	
	private InstrumentData data;
	private String type;
	
}
