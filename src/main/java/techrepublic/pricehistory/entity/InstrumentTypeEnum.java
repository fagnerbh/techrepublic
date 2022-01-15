package techrepublic.pricehistory.entity;

import lombok.Getter;

/**
 * enum for the instrument's payload type
 * @author fagner
 *
 */
@Getter
public enum InstrumentTypeEnum {
	
	ADD("ADD"),
	DELETE("DELETE");
	
	private String type;

	private InstrumentTypeEnum(String type) {
		this.type = type;
	}

}
