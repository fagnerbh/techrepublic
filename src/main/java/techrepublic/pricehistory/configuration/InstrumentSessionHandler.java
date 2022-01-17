package techrepublic.pricehistory.configuration;

import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import lombok.extern.log4j.Log4j2;
import techrepublic.pricehistory.component.interfaces.InstrumentHandler;
import techrepublic.pricehistory.entity.Instrument;

/**
 * WebSocket client handler for partner's instrument endpoint
 * @author fagner
 *
 */
@Log4j2
public class InstrumentSessionHandler extends StompSessionHandlerAdapter {
	
	@Value("${partner.quotes.endpoint}")
	String quotesEndpoint;
	
	@Autowired
	InstrumentHandler instrumentHandler;
	
	private final static Instrument reference = new Instrument();
	
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
	 * incoming instrument data from the websocket is registred in the instrument handler.
	 */
	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		log.info("Client received: payload {}, headers {}", payload, headers);
		instrumentHandler.registerInstrument((Instrument) payload);
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
