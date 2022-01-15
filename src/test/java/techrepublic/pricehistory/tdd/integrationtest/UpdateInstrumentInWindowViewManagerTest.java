package techrepublic.pricehistory.tdd.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import techrepublic.pricehistory.component.interfaces.InstrumentHandler;
import techrepublic.pricehistory.component.interfaces.UpdateNextInstrumentInWindowViewManager;
import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.Instrument;
import techrepublic.pricehistory.entity.InstrumentTypeEnum;
import techrepublic.pricehistory.service.CandleStickWindowViewManager;
import techrepublic.pricehistory.tdd.BaseTest;

class UpdateInstrumentInWindowViewManagerTest extends BaseTest {
	
	@Autowired
	InstrumentHandler instrumentHandler;
	
	@Autowired
	CandleStickWindowViewManager candleStickWindowViewManager;
	
	@Autowired
	UpdateNextInstrumentInWindowViewManager updateNextInstrumentInWindowViewManager;

	@Test
	void updateNextInstrument() {
		instrumentHandler.resetRegisters();
		candleStickWindowViewManager.resetWindows();

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);

		instrumentHandler.registerInstrument(instrument);

		updateNextInstrumentInWindowViewManager.updateNext();
		
		Map<String, ConcurrentLinkedDeque<CandleStick>> mapWindows = candleStickWindowViewManager.getWindows();
		
		assertEquals(mapWindows.size(), 1);
		assertTrue(mapWindows.containsKey("NYB2E0243538"));
	}
	
	@Test
	void updateNullInstrument() {
		instrumentHandler.resetRegisters();
		candleStickWindowViewManager.resetWindows();

		updateNextInstrumentInWindowViewManager.updateNext();

		Map<String, ConcurrentLinkedDeque<CandleStick>> mapWindows = candleStickWindowViewManager.getWindows();

		assertEquals(mapWindows.size(), 0);		
	}
	
	@Test
	void deleteInstrumentInNullWindowView() {
		instrumentHandler.resetRegisters();
		candleStickWindowViewManager.resetWindows();

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.DELETE);

		instrumentHandler.registerInstrument(instrument);

		updateNextInstrumentInWindowViewManager.updateNext();
		
		Map<String, ConcurrentLinkedDeque<CandleStick>> mapWindows = candleStickWindowViewManager.getWindows();
		
		assertEquals(mapWindows.size(), 0);		
	}
	
	@Test
	void updateDeletionExistingWindowView() {
		
		instrumentHandler.resetRegisters();
		candleStickWindowViewManager.resetWindows();

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);

		instrumentHandler.registerInstrument(instrument);

		updateNextInstrumentInWindowViewManager.updateNext();
		
		Map<String, ConcurrentLinkedDeque<CandleStick>> mapWindows = candleStickWindowViewManager.getWindows();
		
		assertEquals(mapWindows.size(), 1);
		assertTrue(mapWindows.containsKey("NYB2E0243538"));
		
		instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.DELETE);
		
		instrumentHandler.registerInstrument(instrument);

		updateNextInstrumentInWindowViewManager.updateNext();
		
		mapWindows = candleStickWindowViewManager.getWindows();
		
		assertEquals(mapWindows.size(), 0);
		
	}
	
	@Test
	void updateAdd2InstrumentsDelete1Existing() {
		instrumentHandler.resetRegisters();
		candleStickWindowViewManager.resetWindows();

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);

		instrumentHandler.registerInstrument(instrument);
		
		instrument = createInstrument("rhoncus porta no lorem", "TY7550534473", InstrumentTypeEnum.ADD);
		
		instrumentHandler.registerInstrument(instrument);

		updateNextInstrumentInWindowViewManager.updateNext();
		updateNextInstrumentInWindowViewManager.updateNext();
		
		Map<String, ConcurrentLinkedDeque<CandleStick>> mapWindows = candleStickWindowViewManager.getWindows();
		
		assertEquals(mapWindows.size(), 2);
		assertTrue(mapWindows.containsKey("NYB2E0243538"));
		assertTrue(mapWindows.containsKey("TY7550534473"));
		
		instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.DELETE);
		
		instrumentHandler.registerInstrument(instrument);

		updateNextInstrumentInWindowViewManager.updateNext();
		
		mapWindows = candleStickWindowViewManager.getWindows();
		
		assertEquals(mapWindows.size(), 1);
		assertTrue(mapWindows.containsKey("TY7550534473"));
		assertFalse(mapWindows.containsKey("NYB2E0243538"));
		
	}

}
