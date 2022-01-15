package techrepublic.pricehistory.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import techrepublic.pricehistory.component.interfaces.InstrumentHandler;
import techrepublic.pricehistory.component.interfaces.UpdateNextInstrumentInWindowViewManager;
import techrepublic.pricehistory.service.CandleStickWindowViewManager;

/**
 * Implementation of the candlestick window view manager update from a queue of received instruments.
 * @author fagner
 *
 */
@Component
public class QueueUpdateNextInstrumentInWindowViewManager implements UpdateNextInstrumentInWindowViewManager {
	
	@Autowired
	InstrumentHandler instrumentHandler;
	
	@Autowired
	CandleStickWindowViewManager candleStickWindowViewManager;

	/**
	 * updates instruments received from partner's instrument endpoint in the injected instance of
	 * the window view manager.
	 */
	@Override
	@Scheduled(cron = "${instrument.window.view.update.interval.cron}")
	public void updateNext() {
		candleStickWindowViewManager.update(instrumentHandler.consumeInstrument());
	}

}
