package techrepublic.pricehistory.tdd.unittest;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Queue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import techrepublic.pricehistory.component.interfaces.InstrumentHandler;
import techrepublic.pricehistory.entity.Instrument;
import techrepublic.pricehistory.entity.InstrumentTypeEnum;
import techrepublic.pricehistory.tdd.BaseTest;

class InstrumentHandlerTest extends BaseTest {
	
	@Autowired
	InstrumentHandler instrumentHandler;

	@Test
	void simpleRegisterTest() {
		instrumentHandler.resetRegisters();
		
		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);
		
		instrumentHandler.registerInstrument(instrument);
		
		Queue<Instrument> queue = instrumentHandler.getCurrentRegisteredInstruments();
		
		assertEquals(queue.size(), 1);
		
		Instrument queueHead = queue.peek();
		
		assertEquals(instrument, queueHead);
	}
	
	@Test
	void nullRegister() {
		instrumentHandler.resetRegisters();
		
		instrumentHandler.registerInstrument(null);

		Queue<Instrument> queue = instrumentHandler.getCurrentRegisteredInstruments();

		assertEquals(queue.size(), 0);
	}
	
	@Test
	void consumeRegistredInstrument() {
		instrumentHandler.resetRegisters();

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);
		instrumentHandler.registerInstrument(instrument);
		
		Instrument nextinstrument = instrumentHandler.consumeInstrument();
		
		assertEquals(instrument, nextinstrument);
		assertEquals(instrumentHandler.getCurrentRegisteredInstruments().size(), 0);
	}
	
	@Test
	void consumeRegistredMoreInstruments() {
		instrumentHandler.resetRegisters();

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);
		instrumentHandler.registerInstrument(instrument);
				
		Instrument instrument2 = createInstrument("sapien tota inani", "CXD82K511660", InstrumentTypeEnum.ADD);
		instrumentHandler.registerInstrument(instrument2);		
				
		Instrument nextinstrument = instrumentHandler.consumeInstrument();
		
		assertEquals(instrument, nextinstrument);
		
		Queue<Instrument> queue = instrumentHandler.getCurrentRegisteredInstruments();
		assertEquals(queue.size(), 1);
		assertEquals(queue.peek().getData().getIsin(), "CXD82K511660");
	}
	
	@Test
	void consumeNullInstruments() {
		instrumentHandler.resetRegisters();
		
		Instrument nextinstrument = instrumentHandler.consumeInstrument();
		
		assertNull(nextinstrument);
	}
	
}
