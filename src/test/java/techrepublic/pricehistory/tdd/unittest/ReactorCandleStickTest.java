package techrepublic.pricehistory.tdd.unittest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.Quote;
import techrepublic.pricehistory.entity.ReactorCandleStick;
import techrepublic.pricehistory.tdd.BaseTest;

class ReactorCandleStickTest extends BaseTest {
	
	CandleStick candleStick;

	@Test
	void testClosedAfterInternalTimerTaskRun() throws InterruptedException {
		Quote incomingQuote = quoteEntity("LAC0642503W4", 1475.7609);
		
		ReactorCandleStick candleStick = new ReactorCandleStick(incomingQuote, 300l);
		Thread.sleep(350l);
		
		assertTrue(candleStick.getClosed());
	}
	
	@Test
	void testNotClosedBeforeInternalTimerTaskRun() throws InterruptedException {
		Quote incomingQuote = quoteEntity("LAC0642503W4", 1475.7609);
		
		ReactorCandleStick candleStick = new ReactorCandleStick(incomingQuote, 300l);
		Thread.sleep(150l);
		
		assertFalse(candleStick.getClosed());
	}

}
