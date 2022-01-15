package techrepublic.pricehistory.component;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import techrepublic.pricehistory.component.interfaces.InstrumentHandler;
import techrepublic.pricehistory.entity.Instrument;
import techrepublic.pricehistory.service.CandleStickWindowViewManager;

/**
 * Implementation of a queue instrument handler where each new instrument registered
 * is added at the tail of a concurrent instrument's queue for further process.
 * @author fagner
 *
 */
@Component
@Log4j2
public class QueueInstrumentHandler implements InstrumentHandler {
	
	@Autowired
	CandleStickWindowViewManager candleStickWindowViewManager;
				
	private static final BlockingQueue<Instrument> instrumentBlockingQueue = new ArrayBlockingQueue<>(50);	
	
	@Override
	public void registerInstrument(Instrument instrument) {		
			// register the next instrument at the tail of the queue in a concurrent way.
			Optional.ofNullable(instrument).ifPresent((inst) -> { try {
				           instrumentBlockingQueue.put(instrument);
			            } catch (InterruptedException e) {
				           log.error("Instrument registration failed for instrument: " + instrument.getData().getIsin());
			            } 
			});			
	}

	@Override
	public Queue<Instrument> getCurrentRegisteredInstruments() {		
		return new ArrayDeque<>(instrumentBlockingQueue);
	}

	@Override
	public void resetRegisters() {
		instrumentBlockingQueue.clear();		
	}

	/**
	 * remove the head ot the instrument queue.
	 */
	@Override
	public Instrument consumeInstrument() {		
		return instrumentBlockingQueue.poll();
	}

}
