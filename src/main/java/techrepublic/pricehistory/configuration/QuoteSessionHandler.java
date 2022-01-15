package techrepublic.pricehistory.configuration;

import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import lombok.extern.log4j.Log4j2;
import techrepublic.pricehistory.component.interfaces.CandleStickPool;
import techrepublic.pricehistory.entity.Quote;

/**
 * WebSocket client handler for partner's quote endpoint
 * @author fagner
 *
 */
@Log4j2
public class QuoteSessionHandler extends StompSessionHandlerAdapter {
	
	@Value("${partner.quotes.endpoint}")
	String quotesEndpoint;
	
	@Autowired
	CandleStickPool candleStickPool;
	
	private final static Quote reference = new Quote();
	
	@Override
	public void afterConnected(StompSession session, StompHeaders headers) {
		log.info("Client connected: headers {}", headers);
		session.subscribe(quotesEndpoint, this);	
	}	

	@Override
	public Type getPayloadType(StompHeaders headers) {		
		return reference.getClass();
	}

	/**
	 * on each payload received, register it in the candlestick payload.
	 */
	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		log.info("Client received: payload {}, headers {}", payload, headers);
		candleStickPool.registerQuote((Quote) payload);
	}

	@Override
	public void handleException(StompSession session, StompCommand command,
			StompHeaders headers, byte[] payload, Throwable exception) {
		log.error("Client error: exception {}, command {}, payload {}, headers {}",
				exception.getMessage(), command, payload, headers);
	} 

	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		log.error("Client transport error: error {}", exception.getMessage());
	}
}
