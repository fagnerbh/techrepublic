package techrepublic.pricehistory.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import techrepublic.pricehistory.component.interfaces.CandleStickPool;
import techrepublic.pricehistory.component.interfaces.UpdateCandleStickWindowView;
import techrepublic.pricehistory.service.CandleStickWindowViewManager;

@Component
public class HashUpdateCandleStickWindowView implements UpdateCandleStickWindowView {
	
	@Autowired
	CandleStickWindowViewManager candleStickWindowViewManager;
	
	@Autowired
	CandleStickPool candleStickPool;

	/**
	 * updates the candlestick window views in CandleStickWindowViewManager with the current candlestick
	 * pool data. After updating, reset the pool for next interval's quote data.
	 */
	@Override
	public void updateWindows() {
		candleStickWindowViewManager.updateWindows(candleStickPool.getPoolSnapShot());
		candleStickPool.resetPool();
	}

}
