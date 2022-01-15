package techrepublic.pricehistory.component.interfaces;

import java.util.Queue;

import techrepublic.pricehistory.entity.Instrument;

/**
 * interface por register incoming instruments from partner's instrument endpoint.
 * 
 * For this contract, implementations must return a Queue of current registrations.
 * @author fagner
 *
 */
public interface InstrumentHandler {
	
	public void registerInstrument(Instrument instrument);
	
	public Queue<Instrument> getCurrentRegisteredInstruments();
	
	public void resetRegisters();

	public Instrument consumeInstrument();

}
