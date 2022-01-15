package techrepublic.pricehistory.tdd.unittest;

import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import techrepublic.pricehistory.component.interfaces.CandleStickPool;
import techrepublic.pricehistory.component.interfaces.CloseSignal;
import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.Quote;
import techrepublic.pricehistory.tdd.BaseTest;

class CloseSignalTest extends BaseTest {
	
	@Autowired
	CandleStickPool candleStickPool;
	
	@Autowired
	CloseSignal closeSignal;

	@Test
	void closeSignalEmptyPool() {
		candleStickPool.resetPool();
		
		closeSignal.onCloseSignal();
	}
	
	@Test
	void closeSignalPoolWithValues() {
		Quote incomingQuote = quoteEntity("LAC0642503W4", 1475.7609);		

		candleStickPool.resetPool();

		candleStickPool.registerQuote(incomingQuote);
		
		closeSignal.onCloseSignal();
		
		CandleStick candleTest = candleStickPool.getCandleStick("LAC0642503W4");
		assertNull(candleTest);
		
	}		
}
