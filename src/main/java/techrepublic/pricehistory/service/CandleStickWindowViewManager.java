package techrepublic.pricehistory.service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.Instrument;

/**
 * Interface for implementing a candle stick view window for the given instruments received.
 * A candle stick window are all candle sticks for the last time interval in minutes, configurable
 * in properties files.
 * @author fagner
 *
 */
public interface CandleStickWindowViewManager {

	public void updateWindows(Map<String, CandleStick> mapCandle);
	
	public Map<String, ConcurrentLinkedDeque<CandleStick>> getWindows();
	
	public void resetWindows();

	public void update(Instrument instrument);

	public Collection<CandleStick> getWindowView(String isin);
}
