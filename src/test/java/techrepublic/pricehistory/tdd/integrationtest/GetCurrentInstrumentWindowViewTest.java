package techrepublic.pricehistory.tdd.integrationtest;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import techrepublic.pricehistory.component.interfaces.CandleStickPool;
import techrepublic.pricehistory.component.interfaces.CloseSignal;
import techrepublic.pricehistory.component.interfaces.InstrumentHandler;
import techrepublic.pricehistory.component.interfaces.UpdateNextInstrumentInWindowViewManager;
import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.Instrument;
import techrepublic.pricehistory.entity.InstrumentTypeEnum;
import techrepublic.pricehistory.entity.Quote;
import techrepublic.pricehistory.service.CandleStickWindowViewManager;
import techrepublic.pricehistory.tdd.BaseTest;

class GetCurrentInstrumentWindowViewTest extends BaseTest {
	
	@Autowired
	CandleStickWindowViewManager candleStickWindowViewManager;
	
	@Autowired
	InstrumentHandler instrumentHandler;
	
	@Autowired
	CandleStickPool candleStickPool;
	
	@Autowired
	CloseSignal closeSignal;
	
	@Autowired
	UpdateNextInstrumentInWindowViewManager updateNextInstrumentInWindowViewManager;

	@Test
	void viewOfAnInsertedInstrument() {
		candleStickWindowViewManager.resetWindows();
		instrumentHandler.resetRegisters();
		candleStickPool.resetPool();
		
		Quote incomingQuote = quoteEntity("NYB2E0243538", 1475.7609);			

		candleStickPool.registerQuote(incomingQuote);

		incomingQuote = quoteEntity("NYB2E0243538", 1475.3);

		candleStickPool.registerQuote(incomingQuote);		

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);

		instrumentHandler.registerInstrument(instrument);
		updateNextInstrumentInWindowViewManager.updateNext();

		closeSignal.onCloseSignal();

		Collection<CandleStick> view = candleStickWindowViewManager.getWindowView("NYB2E0243538");
		
		assertEquals(view.size(), 1);
		
		CandleStick candleView = view.iterator().next();
		assertEquals(candleView.getHighPrice(), 1475.7609);
		assertEquals(candleView.getClosePrice(), 1475.3);
		assertTrue(candleView.getClosed());
	}
	
	@Test
	void viewOfMoreElements() {
		candleStickWindowViewManager.resetWindows();
		instrumentHandler.resetRegisters();
		candleStickPool.resetPool();
		
		Quote incomingQuote = quoteEntity("NYB2E0243538", 1475.7609);			

		candleStickPool.registerQuote(incomingQuote);

		incomingQuote = quoteEntity("NYB2E0243538", 1475.3);

		candleStickPool.registerQuote(incomingQuote);
		candleStickPool.registerQuote(quoteEntity("TND043604228", 370.8491));

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);

		instrumentHandler.registerInstrument(instrument);
		instrumentHandler.registerInstrument(createInstrument("sed suas purus", "TND043604228", InstrumentTypeEnum.ADD));
		
		updateNextInstrumentInWindowViewManager.updateNext();
		updateNextInstrumentInWindowViewManager.updateNext();

		closeSignal.onCloseSignal();

		Collection<CandleStick> view = candleStickWindowViewManager.getWindowView("NYB2E0243538");
		
		assertEquals(candleStickWindowViewManager.getWindows().size(), 2);
		assertEquals(view.size(), 1);
		
		CandleStick candleView = view.iterator().next();
		assertEquals(candleView.getHighPrice(), 1475.7609);
		assertEquals(candleView.getClosePrice(), 1475.3);
		assertTrue(candleView.getClosed());
		
		view = candleStickWindowViewManager.getWindowView("TND043604228");
		
		candleView = view.iterator().next();
		assertEquals(candleView.getHighPrice(), 370.8491);
		assertEquals(candleView.getClosePrice(), 370.8491);
		assertTrue(candleView.getClosed());
	}
	
	@Test
	void insert2InstrumentsAndThenDeletesOneOfThem() {
		candleStickWindowViewManager.resetWindows();
		instrumentHandler.resetRegisters();
		candleStickPool.resetPool();
		
		Quote incomingQuote = quoteEntity("NYB2E0243538", 1475.7609);			

		candleStickPool.registerQuote(incomingQuote);

		incomingQuote = quoteEntity("NYB2E0243538", 1475.3);

		candleStickPool.registerQuote(incomingQuote);
		candleStickPool.registerQuote(quoteEntity("TND043604228", 370.8491));

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);

		instrumentHandler.registerInstrument(instrument);
		instrumentHandler.registerInstrument(createInstrument("sed suas purus", "TND043604228", InstrumentTypeEnum.ADD));
		
		updateNextInstrumentInWindowViewManager.updateNext();
		updateNextInstrumentInWindowViewManager.updateNext();

		//first minute
		closeSignal.onCloseSignal();
		
		candleStickPool.registerQuote(quoteEntity("NYB2E0243538", 1474.7609));
		candleStickPool.registerQuote(quoteEntity("TND043604228", 370.9));
		candleStickPool.registerQuote(quoteEntity("NYB2E0243538", 1476));
		
		instrumentHandler.registerInstrument(createInstrument("sed suas purus", "TND043604228", InstrumentTypeEnum.DELETE));
		
		updateNextInstrumentInWindowViewManager.updateNext();
				
		//second minute
		closeSignal.onCloseSignal();

		Collection<CandleStick> view = candleStickWindowViewManager.getWindowView("NYB2E0243538");
		
		assertEquals(candleStickWindowViewManager.getWindows().size(), 1);
		assertEquals(view.size(), 2);
		
		CandleStick candleView = view.iterator().next();
		assertEquals(candleView.getHighPrice(), 1475.7609);
		assertEquals(candleView.getClosePrice(), 1475.3);
		assertTrue(candleView.getClosed());
		
		candleView = view.iterator().next();
		assertEquals(candleView.getHighPrice(), 1476);
		assertEquals(candleView.getClosePrice(), 1474.7609);
		assertTrue(candleView.getClosed());
		
		assertNull(candleStickWindowViewManager.getWindowView("TND043604228"));
		
		
	}

}
