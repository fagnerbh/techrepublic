package techrepublic.pricehistory.tdd.unittest;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import techrepublic.pricehistory.component.interfaces.InstrumentHandler;
import techrepublic.pricehistory.component.interfaces.UpdateNextInstrumentInWindowViewManager;
import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.Instrument;
import techrepublic.pricehistory.entity.InstrumentTypeEnum;
import techrepublic.pricehistory.entity.Quote;
import techrepublic.pricehistory.service.CandleStickWindowViewManager;
import techrepublic.pricehistory.tdd.BaseTest;

class CandleStickWindowManagerTest extends BaseTest {
	
	@Value("${max.candlestick.window.view.size}")
	int maxWindowSize;
	
	@Autowired
	CandleStickWindowViewManager candleStickWindowViewManager;
	
	@Autowired
	InstrumentHandler instrumentHandler;
	
	@Autowired
	UpdateNextInstrumentInWindowViewManager updateNextInstrumentInWindowViewManager;

	@Test
	void updateIsinWindowViewsWhenCandlePoolIsEmpty() {
		candleStickWindowViewManager.resetWindows();
		candleStickWindowViewManager.updateWindows(null);
		
		assertEquals(0, candleStickWindowViewManager.getWindows().size());
	}
	
	@Test
	void nullUpdateWindowNotEmpty() {
		instrumentHandler.resetRegisters();
		candleStickWindowViewManager.resetWindows();
		
		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "LAC0642503W4", InstrumentTypeEnum.ADD);

		instrumentHandler.registerInstrument(instrument);
		updateNextInstrumentInWindowViewManager.updateNext();
		
        Quote incomingQuote = quoteEntity("LAC0642503W4", 1475.7609);		
		
		Map<String, CandleStick> firstCandleMap = new HashMap<>();
		
		insertQuotesInMap(firstCandleMap, incomingQuote);
		
		candleStickWindowViewManager.updateWindows(firstCandleMap);
		
		candleStickWindowViewManager.updateWindows(null);
		
		Map<String, ConcurrentLinkedDeque<CandleStick>> windows = candleStickWindowViewManager.getWindows();
		assertEquals(windows.get("LAC0642503W4").size(), 2);
		
		CandleStick first = windows.get("LAC0642503W4").peekFirst();
		CandleStick last = windows.get("LAC0642503W4").peekLast();
		
		assertEquals(first.getOpenPrice(), last.getOpenPrice());
		assertEquals(first.getClosePrice(), last.getClosePrice());
		assertEquals(first.getHighPrice(), last.getHighPrice());
		assertEquals(first.getLowPrice(), last.getLowPrice());
		assertEquals(first.getIsin(), last.getIsin());
		
	}
	
	@Test
	void firstUpdate2IsinsSecondUpdate1Isin() {
		instrumentHandler.resetRegisters();
		candleStickWindowViewManager.resetWindows();

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "LAC0642503W4", InstrumentTypeEnum.ADD);

		instrumentHandler.registerInstrument(instrument);

		Instrument instrument2 = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "XX082P751A48", InstrumentTypeEnum.ADD);
		instrumentHandler.registerInstrument(instrument2);		

		Quote firstQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote secondQuote = quoteEntity("XX082P751A48", 1476.7609);

		Map<String, CandleStick> firstCandleMap = new HashMap<>();

		insertQuotesInMap(firstCandleMap, firstQuote, secondQuote);
		
		updateNextInstrumentInWindowViewManager.updateNext();
		updateNextInstrumentInWindowViewManager.updateNext();

		candleStickWindowViewManager.updateWindows(firstCandleMap);

		firstCandleMap = new HashMap<>();
		insertQuotesInMap(firstCandleMap, quoteEntity("LAC0642503W4", 1475.73));

		candleStickWindowViewManager.updateWindows(firstCandleMap);

		Map<String, ConcurrentLinkedDeque<CandleStick>> windows = candleStickWindowViewManager.getWindows();
		assertEquals(windows.get("LAC0642503W4").size(), 2);
		assertEquals(windows.get("XX082P751A48").size(), 2);

		CandleStick first = windows.get("LAC0642503W4").peekFirst();
		CandleStick last = windows.get("LAC0642503W4").peekLast();

		assertEquals(first.getHighPrice(), 1475.7609);
		assertEquals(last.getHighPrice(), 1475.73);
	}

	@Test
	void notSendingIsinSecondTimeButANewIsin() {
		instrumentHandler.resetRegisters();
		candleStickWindowViewManager.resetWindows();
		
		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "LAC0642503W4", InstrumentTypeEnum.ADD);

		instrumentHandler.registerInstrument(instrument);

		Instrument instrument2 = createInstrument("esse tincidunt mnesarchum libero tritani civibus", "XX082P751A48", InstrumentTypeEnum.ADD);
		instrumentHandler.registerInstrument(instrument2);
		
		updateNextInstrumentInWindowViewManager.updateNext();
		updateNextInstrumentInWindowViewManager.updateNext();

		Quote firstQuote = quoteEntity("LAC0642503W4", 1475.7609);
		Quote secondQuote = quoteEntity("XX082P751A48", 1476.7609);

		Map<String, CandleStick> firstCandleMap = new HashMap<>();

		insertQuotesInMap(firstCandleMap, firstQuote, secondQuote);

		candleStickWindowViewManager.updateWindows(firstCandleMap);
		
		Instrument instrument3 = createInstrument("atomorum inani donec", "BAC0642503W4", InstrumentTypeEnum.ADD);
		instrumentHandler.registerInstrument(instrument3);
		updateNextInstrumentInWindowViewManager.updateNext();
		
		firstCandleMap = new HashMap<>();
		insertQuotesInMap(firstCandleMap, quoteEntity("LAC0642503W4", 1475.7609), quoteEntity("BAC0642503W4", 3475.12));
		
		candleStickWindowViewManager.updateWindows(firstCandleMap);
		
		Map<String, ConcurrentLinkedDeque<CandleStick>> windows = candleStickWindowViewManager.getWindows();
		assertEquals(windows.get("LAC0642503W4").size(), 2);
		assertEquals(windows.get("XX082P751A48").size(), 2);
		assertEquals(windows.get("BAC0642503W4").size(), 1);
		
		CandleStick first = windows.get("XX082P751A48").peekFirst();
		CandleStick last = windows.get("XX082P751A48").peekLast();
		
		assertEquals(first.getHighPrice(), 1476.7609);
		assertEquals(last.getHighPrice(), 1476.7609);
		
	}
	
	@Test
	void dropHeadWindowWhenMaxCapacityReached() {
		instrumentHandler.resetRegisters();
		candleStickWindowViewManager.resetWindows();
		
		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "LAC0642503W4", InstrumentTypeEnum.ADD);

		instrumentHandler.registerInstrument(instrument);
		updateNextInstrumentInWindowViewManager.updateNext();

		Quote firstQuote = quoteEntity("LAC0642503W4", 1475.7609);
		
		Map<String, CandleStick> firstCandleMap = new HashMap<>();
		
		insertQuotesInMap(firstCandleMap, firstQuote);

		candleStickWindowViewManager.updateWindows(firstCandleMap);
		candleStickWindowViewManager.updateWindows(firstCandleMap);
		candleStickWindowViewManager.updateWindows(firstCandleMap);
		candleStickWindowViewManager.updateWindows(firstCandleMap);
		
		Map<String, ConcurrentLinkedDeque<CandleStick>> windows = candleStickWindowViewManager.getWindows();
		assertEquals(windows.get("LAC0642503W4").size(), maxWindowSize);
	}
	
	@Test
	void dropHeadWindowWhenMaxCapacityReachedWithValues() {
		instrumentHandler.resetRegisters();
		candleStickWindowViewManager.resetWindows();
		
		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "LAC0642503W4", InstrumentTypeEnum.ADD);

		instrumentHandler.registerInstrument(instrument);
		updateNextInstrumentInWindowViewManager.updateNext();

		Quote firstQuote = quoteEntity("LAC0642503W4", 1475.7609);
		
		Map<String, CandleStick> firstCandleMap = new HashMap<>();
		
		insertQuotesInMap(firstCandleMap, firstQuote);

		candleStickWindowViewManager.updateWindows(firstCandleMap);
		
		firstCandleMap = new HashMap<>();
		insertQuotesInMap(firstCandleMap, quoteEntity("LAC0642503W4", 2000.01));
		candleStickWindowViewManager.updateWindows(firstCandleMap);
		
		firstCandleMap = new HashMap<>();
		insertQuotesInMap(firstCandleMap, quoteEntity("LAC0642503W4", 1000.01));
		candleStickWindowViewManager.updateWindows(firstCandleMap);
		
		firstCandleMap = new HashMap<>();
		insertQuotesInMap(firstCandleMap, quoteEntity("LAC0642503W4", 500.500));
		candleStickWindowViewManager.updateWindows(firstCandleMap);
		
		Map<String, ConcurrentLinkedDeque<CandleStick>> windows = candleStickWindowViewManager.getWindows();
		assertEquals(windows.get("LAC0642503W4").size(), maxWindowSize);
		
		CandleStick first = windows.get("LAC0642503W4").peekFirst();
		assertEquals(first.getHighPrice(), 2000.01);
	}
	
	@Test
	void addingNewWindowViewInManager() {
		candleStickWindowViewManager.resetWindows();
		
		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);
		
		candleStickWindowViewManager.update(instrument);
		
		Map<String, ConcurrentLinkedDeque<CandleStick>> windows = candleStickWindowViewManager.getWindows();
		assertEquals(windows.get("NYB2E0243538").size(), 0);
		assertEquals(windows.get("NYB2E0243538").peek(), null);
		
	}

	@Test
	void tryToAddNewWindowViewInManagerExistingIsinWindow() {
		candleStickWindowViewManager.resetWindows();

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);

		candleStickWindowViewManager.update(instrument);

		Quote incomingQuote = quoteEntity("NYB2E0243538", 1475.7609);		

		Map<String, CandleStick> firstCandleMap = new HashMap<>();

		insertQuotesInMap(firstCandleMap, incomingQuote);

		candleStickWindowViewManager.updateWindows(firstCandleMap);
		
		Instrument instrument2 = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);
		
		candleStickWindowViewManager.update(instrument2);
		
		Map<String, ConcurrentLinkedDeque<CandleStick>> windows = candleStickWindowViewManager.getWindows();
		assertEquals(windows.get("NYB2E0243538").size(), 1);
		
		CandleStick candleTest = windows.get("NYB2E0243538").peek();
		assertEquals(candleTest.getClosePrice(), 1475.7609);

	}
	
	@Test
	void deleteInstrumentWindowView() {

		candleStickWindowViewManager.resetWindows();

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);

		candleStickWindowViewManager.update(instrument);

		Instrument instrument2 = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.DELETE);

		candleStickWindowViewManager.update(instrument2);

		Map<String, ConcurrentLinkedDeque<CandleStick>> windows = candleStickWindowViewManager.getWindows();
		assertNull(windows.get("NYB2E0243538"));
		
	}
	
	@Test
	void deleteNotPresentWindowView() {
		candleStickWindowViewManager.resetWindows();

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.DELETE);

		candleStickWindowViewManager.update(instrument);

		Map<String, ConcurrentLinkedDeque<CandleStick>> windows = candleStickWindowViewManager.getWindows();
		assertNull(windows.get("NYB2E0243538"));		
	}

	private void insertQuotesInMap(Map<String, CandleStick> map, Quote ...quotes) {				
		for (Quote quote: quotes) {
			CandleStick candleTest = new CandleStick(quote);
			map.put(candleTest.getIsin(), candleTest);
		}
	}

}
