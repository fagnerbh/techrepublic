package techrepublic.pricehistory.tdd.integrationtest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import techrepublic.pricehistory.component.interfaces.CandleStickPool;
import techrepublic.pricehistory.component.interfaces.CloseSignal;
import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.Quote;
import techrepublic.pricehistory.service.CandleStickWindowViewManager;
import techrepublic.pricehistory.tdd.BaseTest;

class CloseSignalIntegrationTest extends BaseTest {
	
	@Autowired
	CandleStickPool candleStickPool;
	
	@Autowired
	CloseSignal closeSignal;
	
	@Autowired
	CandleStickWindowViewManager candleStickWindowViewManager;

	@Test
	void testIfCandlesInWindowsClosed() {
		Quote incomingQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote nextQuote = quoteEntity("LBC0642503W4", 1476.7609);

		candleStickPool.resetPool();
		candleStickPool.registerQuote(incomingQuote);
		candleStickPool.registerQuote(nextQuote);

		closeSignal.onCloseSignal();

		Map<String, ConcurrentLinkedDeque<CandleStick>> windowsView = candleStickWindowViewManager.getWindows();

		ConcurrentLinkedDeque<CandleStick> window1 = windowsView.get("LAC0642503W4");
		ConcurrentLinkedDeque<CandleStick> window2 = windowsView.get("LBC0642503W4");
		
		assertTrue(window1.peekFirst().getClosed());
		assertTrue(window2.peekFirst().getClosed());
	}

}
