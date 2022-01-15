package techrepublic.pricehistory.tdd.integrationtest;

import static io.restassured.RestAssured.given;

import java.net.URISyntaxException;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.response.Response;
import techrepublic.pricehistory.component.interfaces.CandleStickPool;
import techrepublic.pricehistory.component.interfaces.CloseSignal;
import techrepublic.pricehistory.component.interfaces.InstrumentHandler;
import techrepublic.pricehistory.component.interfaces.UpdateNextInstrumentInWindowViewManager;
import techrepublic.pricehistory.controller.CandleSticksController;
import techrepublic.pricehistory.entity.Instrument;
import techrepublic.pricehistory.entity.InstrumentTypeEnum;
import techrepublic.pricehistory.entity.Quote;
import techrepublic.pricehistory.service.CandleStickWindowViewManager;
import techrepublic.pricehistory.tdd.BaseTest;

class CandleSticksControllerTest extends BaseTest {

	private MockRestServiceServer mockServer;

	
	private Integer port = 9000;

	@Autowired	
	private RestTemplate restTemplate;	
		
	@Autowired
	CandleSticksController candleSticksController;

	@Value("${application.url.prefix}")
	private String urlPrefix;
	
	@Autowired
	CandleStickWindowViewManager candleStickWindowViewManager;
	
	@Autowired
	InstrumentHandler instrumentHandler;
	
	@Autowired
	CandleStickPool candleStickPool;
	
	@Autowired
	CloseSignal closeSignal;
	
	@Autowired
	UpdateNextInstrumentInWindowViewManager updateNextInstrumentInWindowViewManager;

	@Test
	void returnIsinCandleStickView() throws JsonProcessingException, URISyntaxException {
		RestAssured.port = port;
		mockServer = MockRestServiceServer.createServer(restTemplate);
		
		candleStickWindowViewManager.resetWindows();
		instrumentHandler.resetRegisters();
		candleStickPool.resetPool();
		
		Quote incomingQuote = quoteEntity("NYB2E0243538", 1475.7609);			

		candleStickPool.registerQuote(incomingQuote);

		incomingQuote = quoteEntity("NYB2E0243538", 1475.3);

		candleStickPool.registerQuote(incomingQuote);
		candleStickPool.registerQuote(quoteEntity("TND043604228", 370.8491));

		Instrument instrument = createInstrument("blandit himenaeos noluisse affert omnesque veritus", "NYB2E0243538", InstrumentTypeEnum.ADD);

		instrumentHandler.registerInstrument(instrument);
		instrumentHandler.registerInstrument(createInstrument("sed suas purus", "TND043604228", InstrumentTypeEnum.ADD));
		
		updateNextInstrumentInWindowViewManager.updateNext();
		updateNextInstrumentInWindowViewManager.updateNext();

		closeSignal.onCloseSignal();
		
		Response response = given().contentType("application/json").when()
				.get(urlPrefix + "/candlesticks?isin=" + "NYB2E0243538");
		
		response.then().assertThat().statusCode(HttpStatus.OK.value());
		
		response.then().rootPath("candlesticks.%s").body("", new ResponseAwareMatcher<Response>() {            
			@Override
			public Matcher matcher(Response response) throws Exception {				
				return matcher(response);
			}
       });
	}

}
