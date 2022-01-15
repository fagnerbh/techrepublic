package techrepublic.pricehistory.tdd.integrationtest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.messaging.WebSocketAnnotationMethodMessageHandler;
import org.testcontainers.containers.DockerComposeContainer;

import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.service.CandleStickWindowViewManager;
import techrepublic.pricehistory.tdd.BaseTest;

class WebSocketClientTest extends BaseTest {
	
	public static DockerComposeContainer environment =
		    new DockerComposeContainer(new File("classpath:compose-compose.yml"))
		            .withExposedService("instruments", 8032);
	
	static {		environment.start(); // prevents JUnit of trying to create two instances
		
	}
	
	@Autowired
	@Qualifier("quoteWebSocketClient")
	WebSocketStompClient webSocketStompClient;
	
	@Autowired
	CandleStickWindowViewManager candleStickWindowViewManager;

	@Test
	void test() throws InterruptedException {
		Thread.sleep(120000);
		
		Map<String, ConcurrentLinkedDeque<CandleStick>> windows = candleStickWindowViewManager.getWindows();
		
		assertNotNull(windows);
	}

}
