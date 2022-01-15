package techrepublic.pricehistory.tdd.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import techrepublic.pricehistory.component.interfaces.CandleStickPool;
import techrepublic.pricehistory.component.interfaces.CloseSignal;
import techrepublic.pricehistory.component.interfaces.UpdateCandleStickWindowView;
import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.Quote;
import techrepublic.pricehistory.service.CandleStickWindowViewManager;
import techrepublic.pricehistory.tdd.BaseTest;

class UpdateCandleStickTest extends BaseTest {
	
	@Autowired
	CandleStickPool candleStickPool;
	
	@Autowired
	CandleStickWindowViewManager candleStickWindowViewManager;
	
	@Autowired
	UpdateCandleStickWindowView updateCandleStickWindowView;
	
	@Autowired
	CloseSignal closeSignal;

	@Test
	void simpleWindowUpdateTest() {
		candleStickWindowViewManager.resetWindows();
		
		Quote incomingQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote nextQuote = quoteEntity("LBC0642503W4", 1476.7609);

		candleStickPool.resetPool();
		candleStickPool.registerQuote(incomingQuote);
		candleStickPool.registerQuote(nextQuote);
		
		updateCandleStickWindowView.updateWindows();
		
		assertEquals(candleStickPool.getPoolSnapShot().size(), 0);
		
		Map<String, ConcurrentLinkedDeque<CandleStick>> windowsView = candleStickWindowViewManager.getWindows();
		
		ConcurrentLinkedDeque<CandleStick> window1 = windowsView.get("LAC0642503W4");
		ConcurrentLinkedDeque<CandleStick> window2 = windowsView.get("LBC0642503W4");
		
		assertNotNull(window1);
		assertNotNull(window2);
	}
	
	@Test
	void moreUpdateLoops() {
		candleStickWindowViewManager.resetWindows();
		
		Quote incomingQuote = quoteEntity("LAC0642503W4", 1475.7609);
		
		candleStickPool.resetPool();
		candleStickPool.registerQuote(incomingQuote);
		candleStickPool.registerQuote(quoteEntity("LBC0642503W4", 1476.7609));
		
		candleStickPool.registerQuote(quoteEntity("LAC0642503W4", 1475.8));
		candleStickPool.registerQuote(quoteEntity("LBC0642503W4", 1476.6));
		
		candleStickPool.registerQuote(quoteEntity("LAC0642503W4", 1475.4321));
		candleStickPool.registerQuote(quoteEntity("LBC0642503W4", 1476.931));
		
		closeSignal.onCloseSignal();
		candleStickPool.registerQuote(quoteEntity("LAC0642503W4", 2475.4321));		
				
		candleStickPool.registerQuote(quoteEntity("LAC0642503W4", 1475.7609));
		
		closeSignal.onCloseSignal();
						
		assertEquals(candleStickPool.getPoolSnapShot().size(), 0);
		
		Map<String, ConcurrentLinkedDeque<CandleStick>> windowsView = candleStickWindowViewManager.getWindows();
		
		ConcurrentLinkedDeque<CandleStick> window1 = windowsView.get("LAC0642503W4");
		ConcurrentLinkedDeque<CandleStick> window2 = windowsView.get("LBC0642503W4");
		
		assertNotNull(window1);
		assertNotNull(window2);
		
		assertEquals(window1.size(), 2);
		assertEquals(window2.size(), 2);
		
		CandleStick candleFirst = window1.peekFirst();
		
		assertEquals(candleFirst.getOpenPrice(), 1475.7609);
		assertEquals(candleFirst.getHighPrice(), 1475.8);
		assertEquals(candleFirst.getLowPrice(), 1475.4321);
		assertEquals(candleFirst.getClosePrice(), 1475.4321);
		assertTrue(candleFirst.getClosed());
	}

}
