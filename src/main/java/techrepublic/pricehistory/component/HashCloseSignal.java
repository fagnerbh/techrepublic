package techrepublic.pricehistory.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import techrepublic.pricehistory.component.interfaces.CandleStickPool;
import techrepublic.pricehistory.component.interfaces.CloseSignal;
import techrepublic.pricehistory.component.interfaces.UpdateCandleStickWindowView;
import techrepublic.pricehistory.service.CandleStickWindowViewManager;

/**
 * Implementaion of CloseSignal interface. On onCloseSignal call, it closes all existing
 * candlestick in the candlestick pool. So, despite the candle stick pool keeps receiving feed
 * from the partner's endpoint, the current candlestick won't be updated anymore.
 * 
 * this method call is scheduled by the system to occur on each interval of the propertie candlestick.close.interval
 * 
 * @author fagner
 *
 */
@Component
public class HashCloseSignal implements CloseSignal {
	
	@Autowired
	CandleStickPool candleStickPool;
	
	@Autowired
	CandleStickWindowViewManager candleStickWindowViewManager;
	
	@Autowired
	UpdateCandleStickWindowView updateCandleStickWindowView;

	@Override
	@Scheduled(cron = "${candlestick.close.interval.cron}")
	public void onCloseSignal() {
		candleStickPool.closePoolForNewQuotes();		
		updateCandleStickWindowView.updateWindows();
	}

}
