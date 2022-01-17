package techrepublic.pricehistory.entity;

import java.util.Date;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CandleStick implements CandleStickInterface {
	
	private Date openTimestamp;
	private volatile Double openPrice;
	private volatile Double highPrice;
	private volatile Double lowPrice;
	private volatile Double closePrice;
	private volatile Date closeTimestamp;
	private final String isin;
	private volatile Boolean closed;
	
	/**
	 * creates a new candle stick from an incoming quote.
	 * @param quote
	 */
	public CandleStick(Quote quote) {
		openTimestamp = new Date();
		openPrice = highPrice = lowPrice = closePrice =  quote.getData().getPrice();
		isin = quote.getData().getIsin();
		closed = false;
	}
	
	/**
	 * creates a new candle stick with a given isin.
	 * @param quote
	 */
	public CandleStick(String isin) {		
		this.isin = isin;
		closed = false;
	}
	
	/**
	 * updates the candlestick info with new quote for the correct instrument which came
	 * from the partner's endpoint.
	 * @param quote
	 */
	public void registerNewQuote(Quote quote) {
		if (!closed) {
			Optional<Quote> optQuote = Optional.ofNullable(quote);

			if (!optQuote.isEmpty()) {
				Quote incomingQuote = optQuote.get();

				if (!isin.equals(incomingQuote.getData().getIsin())) {
					return;
				}

				Double incomingPrice = optQuote.get().getData().getPrice();

				// incoming quote's price from partner's source is the new candle's highprice
				if (highPrice == null) {
					highPrice = lowPrice = openPrice = closePrice = incomingPrice;
					openTimestamp = new Date();
				} else {
					if (highPrice.compareTo(incomingPrice) <= 0) {
						highPrice = incomingPrice;
					} else if (lowPrice.compareTo(incomingPrice) >= 0) {
						lowPrice = incomingPrice;
					}
				}

				closePrice = incomingPrice;
			}
		}
		
	}
	
	/**
	 * closes the candlestick, if it is already open. Register the close timestamp and flags this instance to not update its data with
	 * new incoming quotes data.
	 */
	public void close() {
		if (openTimestamp != null) {
			closeTimestamp = new Date();
			closed = true;
		}
	}
	

	@Override
	public Object clone() throws CloneNotSupportedException {		
		return (CandleStick) super.clone();
	}
	
	
	
}
