package techrepublic.pricehistory.controller;

import java.text.DateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import techrepublic.pricehistory.controller.response.CandleStickDto;
import techrepublic.pricehistory.controller.response.CandleSticksResponse;
import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.service.CandleStickWindowViewManager;

@RestController
public class CandleSticksController {
	
	@Autowired
	CandleStickWindowViewManager candleStickWindowViewManager;
	
	@GetMapping("/candlesticks")
    public ResponseEntity<CandleSticksResponse> retrieve(@RequestParam(value = "isin", required = true) String isin, HttpServletRequest request) {
		
		Optional<Collection<CandleStick>> originalView = Optional.ofNullable(candleStickWindowViewManager.getWindowView(isin));
		
		final CandleSticksResponse response;
		final List<CandleStickDto> listDto;
				
		if (originalView.isPresent()) {
			listDto = originalView.get().stream().map((candle) -> {
				CandleStickDto dto = new CandleStickDto();
								
				dto.setCloseTimestamp(DateFormat.getDateTimeInstance().format(candle.getCloseTimestamp()));
				dto.setOpenTimestamp(DateFormat.getDateTimeInstance().format(candle.getOpenTimestamp()));
				dto.setClosePrice(candle.getClosePrice());
				dto.setOpenPrice(candle.getOpenPrice());
				dto.setHighPrice(candle.getHighPrice());
				dto.setLowPrice(candle.getLowPrice());
				
				return dto;
			}).collect(Collectors.toList());
			
			response = new CandleSticksResponse();
			response.setCandlesticks(listDto);
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		}	
		
		response = new CandleSticksResponse();
		return new ResponseEntity<>(response, HttpStatus.OK);		
	}

}
