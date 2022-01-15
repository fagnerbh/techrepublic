package techrepublic.pricehistory.tdd.unittest;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.Quote;
import techrepublic.pricehistory.tdd.BaseTest;

class CandleStickCreateTest extends BaseTest {

	@Test
	void creatingNewCandleStick() {
		Quote incomingQuote = quoteEntity("LAC0642503W4", 1475.7609);
		
		CandleStick candleTest = new CandleStick(incomingQuote);
		
		//assertEquals(new Date().getTime(), candleTest.getOpenTimestamp().getTime()); replace with mockito
		assertEquals(1475.7609, candleTest.getOpenPrice().doubleValue());
		assertEquals(1475.7609, candleTest.getClosePrice().doubleValue());
		assertEquals(1475.7609, candleTest.getHighPrice().doubleValue());
		assertEquals(1475.7609, candleTest.getLowPrice().doubleValue());
		assertEquals("LAC0642503W4", candleTest.getIsin());
		assertNull(candleTest.getCloseTimestamp());
		
	}

	@Test
	void updateCandleStickQuoteBiggerPrice() {
		Quote initialQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote incomingQuote = quoteEntity("LAC0642503W4", 1476.7609);

		CandleStick candleTest = new CandleStick(initialQuote);
		
		candleTest.registerNewQuote(incomingQuote);
		
		assertEquals(1475.7609, candleTest.getOpenPrice().doubleValue());
		assertEquals(1476.7609, candleTest.getClosePrice().doubleValue());
		assertEquals(1476.7609, candleTest.getHighPrice().doubleValue());
		assertEquals(1475.7609, candleTest.getLowPrice().doubleValue());
		assertNull(candleTest.getCloseTimestamp());

	}
	
	@Test
	void updateCandleStickQuoteBiggerPriceDifferentIsin() {
		Quote initialQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote incomingQuote = quoteEntity("XX082P751A48", 1476.7609);

		CandleStick candleTest = new CandleStick(initialQuote);
		
		candleTest.registerNewQuote(incomingQuote);
		
		assertEquals(1475.7609, candleTest.getOpenPrice().doubleValue());
		assertEquals(1475.7609, candleTest.getClosePrice().doubleValue());
		assertEquals(1475.7609, candleTest.getHighPrice().doubleValue());
		assertEquals(1475.7609, candleTest.getLowPrice().doubleValue());
		assertEquals("LAC0642503W4", candleTest.getIsin());
		assertNull(candleTest.getCloseTimestamp());

	}
	
	@Test
	void updateCandleStickQuoteSmallerPrice() {
		Quote initialQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote incomingQuote = quoteEntity("LAC0642503W4", 1475.7606);

		CandleStick candleTest = new CandleStick(initialQuote);
		
		candleTest.registerNewQuote(incomingQuote);
		
		assertEquals(1475.7609, candleTest.getOpenPrice().doubleValue());
		assertEquals(1475.7606, candleTest.getClosePrice().doubleValue());
		assertEquals(1475.7609, candleTest.getHighPrice().doubleValue());
		assertEquals(1475.7606, candleTest.getLowPrice().doubleValue());
		assertNull(candleTest.getCloseTimestamp());
	}
	
	@Test
	void updateCandleStickQuote1HighPriceQuote2SmallPrice() {
		Quote initialQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote higherQuote = quoteEntity("LAC0642503W4", 1475.77);
		Quote smallerQuote = quoteEntity("LAC0642503W4", 1475.7606);

		CandleStick candleTest = new CandleStick(initialQuote);
		
		candleTest.registerNewQuote(higherQuote);
		candleTest.registerNewQuote(smallerQuote);
		
		assertEquals(1475.7609, candleTest.getOpenPrice().doubleValue());
		assertEquals(1475.7606, candleTest.getClosePrice().doubleValue());
		assertEquals(1475.77, candleTest.getHighPrice().doubleValue());
		assertEquals(1475.7606, candleTest.getLowPrice().doubleValue());
		assertNull(candleTest.getCloseTimestamp());
	}
	
	@Test
	void updateCandleStickQuote1HighPriceQuote2SmallPriceQuote3Unchange() {
		Quote initialQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote higherQuote = quoteEntity("LAC0642503W4", 1475.77);
		Quote smallerQuote = quoteEntity("LAC0642503W4", 1475.7606);
		Quote unchangeQuote = quoteEntity("LAC0642503W4", 1475.763);

		CandleStick candleTest = new CandleStick(initialQuote);
		
		candleTest.registerNewQuote(higherQuote);
		candleTest.registerNewQuote(smallerQuote);
		candleTest.registerNewQuote(unchangeQuote);
		
		assertEquals(1475.7609, candleTest.getOpenPrice().doubleValue());
		assertEquals(1475.763, candleTest.getClosePrice().doubleValue());
		assertEquals(1475.77, candleTest.getHighPrice().doubleValue());
		assertEquals(1475.7606, candleTest.getLowPrice().doubleValue());
		assertNull(candleTest.getCloseTimestamp());
	}
	
	@Test
	void closeCandle() {
		
		Quote initialQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote higherQuote = quoteEntity("LAC0642503W4", 1475.77);
		Quote smallerQuote = quoteEntity("LAC0642503W4", 1475.7606);
		Quote unchangeQuote = quoteEntity("LAC0642503W4", 1475.763);

		CandleStick candleTest = new CandleStick(initialQuote);
		
		candleTest.registerNewQuote(higherQuote);
		candleTest.registerNewQuote(smallerQuote);
		candleTest.registerNewQuote(unchangeQuote);
		candleTest.close();
		
		assertEquals(1475.7609, candleTest.getOpenPrice().doubleValue());
		assertEquals(1475.763, candleTest.getClosePrice().doubleValue());
		assertEquals(1475.77, candleTest.getHighPrice().doubleValue());
		assertEquals(1475.7606, candleTest.getLowPrice().doubleValue());
		assertNotNull(candleTest.getCloseTimestamp());
		assertTrue(candleTest.getClosed());
		
	}
	
	@Test
	void inComingQuoteAfterCandleClosed() {
		Quote initialQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote higherQuote = quoteEntity("LAC0642503W4", 1475.77);
		Quote smallerQuote = quoteEntity("LAC0642503W4", 1475.7606);
		Quote unchangeQuote = quoteEntity("LAC0642503W4", 1475.763);

		CandleStick candleTest = new CandleStick(initialQuote);
		
		candleTest.registerNewQuote(higherQuote);
		candleTest.registerNewQuote(smallerQuote);
		candleTest.registerNewQuote(unchangeQuote);
		candleTest.close();
		
		Quote afterClose1 = quoteEntity("LAC0642503W4", 1575.763);
		Quote afterClose2 = quoteEntity("LAC0642503W4", 1375.763);
		
		candleTest.registerNewQuote(afterClose1);
		candleTest.registerNewQuote(afterClose2);
		
		assertEquals(1475.7609, candleTest.getOpenPrice().doubleValue());
		assertEquals(1475.763, candleTest.getClosePrice().doubleValue());
		assertEquals(1475.77, candleTest.getHighPrice().doubleValue());
		assertEquals(1475.7606, candleTest.getLowPrice().doubleValue());
		assertNotNull(candleTest.getCloseTimestamp());
		assertTrue(candleTest.getClosed());
	}

}
