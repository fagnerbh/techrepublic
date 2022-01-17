package techrepublic.pricehistory.component;

import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import techrepublic.pricehistory.component.interfaces.CandleStickPool;
import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.InstrumentTypeEnum;
import techrepublic.pricehistory.entity.Quote;

/**
 * Candle Stick pool where each distinct ISIN represents a key in the hash.
 * this class will receive the next Quote from partner's quote endpoint and register it
 * on the respective Quote's ISIN hash bucket in the hash.
 * 
 * @author fagner
 *
 */
@Component
@Qualifier("hashCandleStickPool")
public class HashCandleStickPool implements CandleStickPool {
	
	private static final ConcurrentHashMap<String, CandleStick> hashPool;
	
	static {
		hashPool = new ConcurrentHashMap<>();
	}

	/**
	 * register the next quote from the quotes endpoint in the candlestick in the pool
	 * with the same ISIN.
	 * 
	 * @param quote - the next quote from endpoint
	 */
	@Override
	public void registerQuote(Quote quote) {
		if (quote == null) {
			return;
		}

		String currentIsin = quote.getData().getIsin();

		synchronized (hashPool) {
			if (hashPool.containsKey(currentIsin)) {
				CandleStick currentCandle = hashPool.get(currentIsin);

				if (!currentCandle.getClosed()) {
					currentCandle.registerNewQuote(quote);
				}
			}
		}
	}

	@Override
	public void resetPool() {		
		hashPool.clear();		
	}

	@Override
	public CandleStick getCandleStick(String isin) {
		Optional<CandleStick> candle = Optional.ofNullable(hashPool.get(isin));
		
		if (!candle.isEmpty()) {
			try {
				return (CandleStick) candle.get().clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
		
		return null;
	}

	/**
	 * closes all current candlesticks in the pool.
	 */
	@Override
	public void closePoolForNewQuotes() {
		Enumeration<String> enumIsin = hashPool.keys();
		
		while (enumIsin.hasMoreElements()) {
			Optional.ofNullable(hashPool.get(enumIsin.nextElement())).ifPresent((candle) -> candle.close());
		}
	}

	/**
	 * returns a copy of pool at the moment.
	 */
	@Override
	public Map<String, CandleStick> getPoolSnapShot() {		
		return new ConcurrentHashMap<>(hashPool);				
	}

	/**
	 * updates a pool with an incoming instrument. 
	 */
	@Override
	public void updatePool(CandleStick candleStick, InstrumentTypeEnum type) {
		Optional.ofNullable(candleStick).ifPresent((candle) -> {
			String newIsin = candle.getIsin();
			
			synchronized (hashPool) {			
				if (type == InstrumentTypeEnum.ADD && 
						!hashPool.contains(newIsin)) {
					hashPool.put(newIsin, candle);
				} else if (type == InstrumentTypeEnum.DELETE) {
					hashPool.remove(newIsin);
				}
			}
		});
		
	}

	@Override
	public void removeCandleStick(String isin) {
		synchronized (hashPool) {
			hashPool.remove(isin);
		}		
	}
	
}
