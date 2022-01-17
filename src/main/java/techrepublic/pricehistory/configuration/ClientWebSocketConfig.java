package techrepublic.pricehistory.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

/**
 * configure WebSocket clients to listen to partner's endpoints.
 * @author fagner
 *
 */
@Configuration
public class ClientWebSocketConfig {
	
	@Value("${partner.quotes.endpoint}")
	String quotesEndpoint;
	
	@Value("${partner.instruments.endpoint}")
	String instrumentEndpoint;

	@Bean("quoteWebSocketClient")
	public WebSocketStompClient quoteWebSocketClient() {
		WebSocketStompClient webSocketStompClient = new WebSocketStompClient(webSocketClient());
		webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter()); // added: setting the converter
		webSocketStompClient.connect(quotesEndpoint, quoteStompSessionHandler());
		return webSocketStompClient;
	}    

	@Bean("instrumentWebSocketClient")
	public WebSocketStompClient instrumentWebSocketClient() {
		WebSocketStompClient webSocketStompClient = new WebSocketStompClient(webSocketClient());
		webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter()); // added: setting the converter
		webSocketStompClient.connect(instrumentEndpoint, instrumentStompSessionHandler());
		return webSocketStompClient;
	}

	private StompSessionHandler quoteStompSessionHandler() {
		return new QuoteSessionHandler();
	}
	
	private StompSessionHandler instrumentStompSessionHandler() {
		return new InstrumentSessionHandler();
	}

	private SockJsClient webSocketClient() {
		List<Transport> transports = new ArrayList<>();
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		transports.add(new RestTemplateXhrTransport());
		return new SockJsClient(transports);
	}

}
