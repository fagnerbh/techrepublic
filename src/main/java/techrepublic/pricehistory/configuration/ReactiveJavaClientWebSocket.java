package techrepublic.pricehistory.configuration;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.google.gson.Gson;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import techrepublic.pricehistory.component.ReactiveCandleStickPool;
import techrepublic.pricehistory.component.interfaces.InstrumentHandler;
import techrepublic.pricehistory.entity.Instrument;
import techrepublic.pricehistory.entity.InstrumentTypeEnum;
import techrepublic.pricehistory.entity.Quote;
import techrepublic.pricehistory.entity.ReactorCandleStick;
import techrepublic.pricehistory.service.ReactorCandleStickWindowViewManager;

@Configuration
public class ReactiveJavaClientWebSocket {

	@Value("${partner.quotes.endpoint}")
	String quotesEndpoint;

	@Value("${partner.instruments.endpoint}")
	String instrumentEndpoint;
	
	@Autowired
	@Qualifier("reactiveCandleStickPool")
	ReactiveCandleStickPool candleStickPool;
	
	@Autowired
	InstrumentHandler instrumentHandler;
	
	@Autowired
	@Qualifier("reactorCandleStickWindowViewManager")
	ReactorCandleStickWindowViewManager reactorCandleStickWindowViewManager;

	final static Gson gson = new Gson();

	@Bean("quoteReactiveWebSocketClient")
	public WebSocketClient quoteReactiveWebSocketClient() {
		WebSocketClient client = new ReactorNettyWebSocketClient();

		client.execute(URI.create(quotesEndpoint), session -> 
		session.receive().onErrorContinue((t, o) -> t.printStackTrace())
		.publishOn(Schedulers.boundedElastic())
		.map((payload) -> {
			Quote quote = gson.fromJson(payload.getPayloadAsText(), Quote.class);
			Mono<Quote> monoQuote = Mono.just(quote);
			candleStickPool.registerQuote(monoQuote);

			return Mono.just(monoQuote);
		}).then());


		return client;

	}
	
	@Bean("instrumentReactiveWebSocketClient")
	public WebSocketClient instrumentReactiveWebSocketClient() {
		WebSocketClient client = new ReactorNettyWebSocketClient();		
		
		client.execute(URI.create(instrumentEndpoint), session -> 
		session.receive().onErrorContinue((t, o) -> t.printStackTrace())		
		.map((payload) -> {
			Instrument instrument = gson.fromJson(payload.getPayloadAsText(), Instrument.class);
			reactorCandleStickWindowViewManager.update(instrument);
			
			ReactorCandleStick reactCanldeStick = new ReactorCandleStick(instrument.getData().getIsin());
			reactorCandleStickWindowViewManager.subscribeToCandleStick(reactCanldeStick.getClosedPublisher());
			candleStickPool.updatePool(reactCanldeStick, InstrumentTypeEnum.valueOf(instrument.getType()));
			
			return Mono.just(candleStickPool.getCandleStick(instrument.getData().getIsin()));
		}).then());

		return client;

	}

}
