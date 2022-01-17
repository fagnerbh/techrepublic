package techrepublic.pricehistory.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import techrepublic.pricehistory.component.ReactiveCandleStickPool;
import techrepublic.pricehistory.entity.InstrumentTypeEnum;
import techrepublic.pricehistory.entity.ReactorCandleStick;

@Service
@Qualifier("reactorCandleStickWindowViewManager")
public class ReactorCandleStickWindowViewManager extends HashCandleStickWindowViewManager {
	
	protected Flux<ReactorCandleStick> updateFlux;
	
	@Autowired
	@Qualifier("reactiveCandleStickPool")
	ReactiveCandleStickPool reactiveCandleStickPool;
	
	public void subscribeToCandleStick(Mono<ReactorCandleStick> candleStick) {
		Optional.ofNullable(updateFlux).ifPresentOrElse((flux -> flux.concatWith(candleStick)),
				() -> {
					updateFlux = candleStick.flatMapMany(c -> Mono.just(c));
					updateFlux.subscribe((candle) -> {
						if (candle.getClosed() && !Optional.ofNullable(candle.getOpenTimestamp()).isEmpty()) {										
							this.updateWindow(candle);							
						}

						synchronized (reactiveCandleStickPool) {
							//resets this candle pool to accept the next minute's quotes.
							reactiveCandleStickPool.updatePool(new ReactorCandleStick(candle.getIsin()), InstrumentTypeEnum.ADD);
						}						
					});
				});
	}

}
