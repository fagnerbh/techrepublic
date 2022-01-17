package techrepublic.pricehistory.tdd.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import techrepublic.pricehistory.component.interfaces.CandleStickPool;
import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.InstrumentTypeEnum;
import techrepublic.pricehistory.entity.Quote;
import techrepublic.pricehistory.tdd.BaseTest;

class CandleStickPoolTest extends BaseTest {
	
	@Autowired
	CandleStickPool candleStickPool;	

	@Test
	void registerNewQuoteNewBucketInHash() {
		Quote incomingQuote = quoteEntity("TAC0642503W4", 1475.7609);		

		candleStickPool.resetPool();		
						
		candleStickPool.updatePool(new CandleStick("TAC0642503W4"), InstrumentTypeEnum.ADD);
				
		candleStickPool.registerQuote(incomingQuote);
		
		CandleStick candleTest = candleStickPool.getCandleStick("TAC0642503W4");
		
		assertEquals(1475.7609, candleTest.getOpenPrice());
		assertEquals(1475.7609, candleTest.getClosePrice());
		assertEquals(1475.7609, candleTest.getHighPrice());
		assertEquals(1475.7609, candleTest.getLowPrice());
		assertEquals("TAC0642503W4", candleTest.getIsin());
		assertNull(candleTest.getCloseTimestamp());

	}
	
	@Test
	void registerNewQuoteExistingOpenCandleInHash() {
		Quote incomingQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote nextQuote = quoteEntity("LAC0642503W4", 1476.7609);

		candleStickPool.resetPool();
		candleStickPool.updatePool(new CandleStick("LAC0642503W4"), InstrumentTypeEnum.ADD);
		candleStickPool.registerQuote(incomingQuote);
		candleStickPool.registerQuote(nextQuote);
		
		CandleStick candleTest = candleStickPool.getCandleStick("LAC0642503W4");
		
		assertEquals(1475.7609, candleTest.getOpenPrice().doubleValue());
		assertEquals(1476.7609, candleTest.getClosePrice().doubleValue());
		assertEquals(1476.7609, candleTest.getHighPrice().doubleValue());
		assertEquals(1475.7609, candleTest.getLowPrice().doubleValue());
		assertNull(candleTest.getCloseTimestamp());
		
	}
	
	@Test
	void registerQuotesAfterCloseSignal() {
		Quote incomingQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote nextQuote = quoteEntity("LAC0642503W4", 1476.7609);

		candleStickPool.resetPool();
		candleStickPool.updatePool(new CandleStick("LAC0642503W4"), InstrumentTypeEnum.ADD);
		candleStickPool.registerQuote(incomingQuote);
		candleStickPool.registerQuote(nextQuote);
		
		candleStickPool.closePoolForNewQuotes();
		
		Quote afterClose1 = quoteEntity("LAC0642503W4", 1575.763);
		Quote afterClose2 = quoteEntity("LAC0642503W4", 1375.763);
		
		candleStickPool.registerQuote(afterClose1);
		candleStickPool.registerQuote(afterClose2);
		
		CandleStick candleTest = candleStickPool.getCandleStick("LAC0642503W4");
		
		assertEquals(1475.7609, candleTest.getOpenPrice().doubleValue());
		assertEquals(1476.7609, candleTest.getClosePrice().doubleValue());
		assertEquals(1476.7609, candleTest.getHighPrice().doubleValue());
		assertEquals(1475.7609, candleTest.getLowPrice().doubleValue());
		assertNotNull(candleTest.getCloseTimestamp());
		
	}
	
	@Test
	void testAllCandlesClosedAfterCloseSignal() {
		Quote incomingQuote = quoteEntity("PAC0642503W4", 1475.7609);
		Quote nextQuote = quoteEntity("LBC0642503W4", 1476.7609);

		candleStickPool.resetPool();
		candleStickPool.updatePool(new CandleStick("PAC0642503W4"), InstrumentTypeEnum.ADD);
		candleStickPool.updatePool(new CandleStick("LBC0642503W4"), InstrumentTypeEnum.ADD);
		candleStickPool.registerQuote(incomingQuote);
		candleStickPool.registerQuote(nextQuote);
		
		candleStickPool.closePoolForNewQuotes();
		
		CandleStick incomingCandle = candleStickPool.getCandleStick("PAC0642503W4");
		CandleStick nextCandle = candleStickPool.getCandleStick("LBC0642503W4");
		
		assertTrue(incomingCandle.getClosed());
		assertTrue(nextCandle.getClosed());
	}
	
	@Test
	void testResetPool() {
		Quote incomingQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote nextQuote = quoteEntity("LBC0642503W4", 1476.7609);

		candleStickPool.resetPool();
		candleStickPool.registerQuote(incomingQuote);
		candleStickPool.registerQuote(nextQuote);
		
		candleStickPool.resetPool();
		
		assertNull(candleStickPool.getCandleStick("LAC0642503W4"));
		assertNull(candleStickPool.getCandleStick("LBC0642503W4"));
	}
	
	@Test
	void poolSnapShotTest() {
		Quote incomingQuote = quoteEntity("SAC0642503W4", 1475.7609);
		Quote nextQuote = quoteEntity("LSC0642503W4", 1476.7609);

		candleStickPool.resetPool();
		candleStickPool.updatePool(new CandleStick("SAC0642503W4"), InstrumentTypeEnum.ADD);
		candleStickPool.updatePool(new CandleStick("LSC0642503W4"), InstrumentTypeEnum.ADD);
		candleStickPool.registerQuote(incomingQuote);
		candleStickPool.registerQuote(nextQuote);
		
		Map<String, CandleStick> mapCopy = candleStickPool.getPoolSnapShot();
		
		CandleStick copyIncomingQuote = mapCopy.get("SAC0642503W4");
		
		assertEquals(1475.7609, copyIncomingQuote.getOpenPrice());
		assertEquals(1475.7609, copyIncomingQuote.getClosePrice());
		assertEquals(1475.7609, copyIncomingQuote.getHighPrice());
		assertEquals(1475.7609, copyIncomingQuote.getLowPrice());
		assertEquals("SAC0642503W4", copyIncomingQuote.getIsin());
		assertNull(copyIncomingQuote.getCloseTimestamp());
		
		CandleStick copyNextQuote = mapCopy.get("LSC0642503W4");
		
		assertEquals(1476.7609, copyNextQuote.getOpenPrice());
		assertEquals(1476.7609, copyNextQuote.getClosePrice());
		assertEquals(1476.7609, copyNextQuote.getHighPrice());
		assertEquals(1476.7609, copyNextQuote.getLowPrice());
		assertEquals("LSC0642503W4", copyNextQuote.getIsin());
		assertNull(copyNextQuote.getCloseTimestamp());
	}

}
