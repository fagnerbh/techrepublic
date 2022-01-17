package techrepublic.pricehistory.component;

import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import techrepublic.pricehistory.component.interfaces.CandleStickPool;
import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.InstrumentTypeEnum;
import techrepublic.pricehistory.entity.Quote;
import techrepublic.pricehistory.entity.ReactorCandleStick;

@Component
@Qualifier("reactiveCandleStickPool")
public class ReactiveCandleStickPool implements CandleStickPool {

	private static final ConcurrentHashMap<String, ReactorCandleStick> hashPool;

	static {
		hashPool = new ConcurrentHashMap<>();
	}

	public void registerQuote(Mono<Quote> quote) {

		quote.subscribe(q -> registerQuote(q));

	}

	@Override
	public void registerQuote(Quote quote) {
		if (quote == null) {
			return;
		}

		String currentIsin = quote.getData().getIsin();

		synchronized (hashPool) {
			if (hashPool.containsKey(currentIsin)) {
				ReactorCandleStick currentCandle = hashPool.get(currentIsin);

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
		Optional<ReactorCandleStick> candle = Optional.ofNullable(hashPool.get(isin));

		if (!candle.isEmpty()) {
			try {
				return (CandleStick) candle.get().clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}

		return null;
	}

	@Override
	public void closePoolForNewQuotes() {
		Enumeration<String> enumIsin = hashPool.keys();

		while (enumIsin.hasMoreElements()) {
			Optional.ofNullable(hashPool.get(enumIsin.nextElement())).ifPresent((candle) -> candle.close());
		}
	}

	@Override
	public Map<String, CandleStick> getPoolSnapShot() {
		return new ConcurrentHashMap<>(hashPool);
	}

	@Override
	public void updatePool(CandleStick candleStick, InstrumentTypeEnum type) {
		Optional.ofNullable(candleStick).ifPresent((candle) -> {
			String newIsin = candle.getIsin();

			synchronized (hashPool) {			
				if (type == InstrumentTypeEnum.ADD && 
						!hashPool.contains(newIsin)) {
					hashPool.put(newIsin, (ReactorCandleStick) candle);
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
