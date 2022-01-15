package techrepublic.pricehistory.tdd.unittest;

import java.text.DateFormat;

import org.junit.jupiter.api.Test;

import techrepublic.pricehistory.controller.response.CandleStickDto;
import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.Quote;
import techrepublic.pricehistory.tdd.BaseTest;

class CandleStickDtoTest extends BaseTest {

	@Test
	void testDtoCreation() {
		Quote incomingQuote = quoteEntity("LAC0642503W4", 1475.7609);

		CandleStick candle = new CandleStick(incomingQuote);
		candle.close();

		CandleStickDto dto = new CandleStickDto();

		dto.setCloseTimestamp(DateFormat.getDateTimeInstance().format(candle.getCloseTimestamp()));
		dto.setOpenTimestamp(DateFormat.getDateTimeInstance().format(candle.getOpenTimestamp()));
		dto.setClosePrice(candle.getClosePrice());
		dto.setOpenPrice(candle.getOpenPrice());
		dto.setHighPrice(candle.getHighPrice());
		dto.setLowPrice(candle.getLowPrice());
	}

}
