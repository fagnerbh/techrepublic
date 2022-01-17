package techrepublic.pricehistory.component.interfaces;

import java.util.Map;

import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.InstrumentTypeEnum;
import techrepublic.pricehistory.entity.Quote;

/**
 * Interface for managing the current minute active candlestick pool.
 * @author fagner
 *
 */
public interface CandleStickPool {
	
	public void registerQuote(Quote quote);
	
	public void resetPool();
	
	public CandleStick getCandleStick(String isin);
	
	public void closePoolForNewQuotes();
	
	public Map<String, CandleStick> getPoolSnapShot();

	public void updatePool(CandleStick createInstrument, InstrumentTypeEnum type);
	
	public void removeCandleStick(String isin);
}
