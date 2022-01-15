package techrepublic.pricehistory.tdd;

import techrepublic.pricehistory.entity.Instrument;
import techrepublic.pricehistory.entity.InstrumentData;
import techrepublic.pricehistory.entity.InstrumentTypeEnum;
import techrepublic.pricehistory.entity.Quote;
import techrepublic.pricehistory.entity.QuoteData;

public class BaseTest extends PriceHistoryApplicationTests{

	private static final String QUOTE = "QUOTE";

	protected Quote quoteEntity(String isin, double price) {
		Quote incomingQuote = new Quote();
		incomingQuote.setType(QUOTE);
		QuoteData quoteData = new QuoteData();
		
		quoteData.setIsin(isin);
		quoteData.setPrice(price);
		incomingQuote.setData(quoteData);
		return incomingQuote;
	}
	
	protected Instrument createInstrument(String description, String isin, InstrumentTypeEnum type) {
		InstrumentData insData = new InstrumentData();
		Instrument instrument = new Instrument();
		instrument.setType(type.getType());
		
		insData.setDescription(description);
		insData.setIsin(isin);
		instrument.setData(insData);
		return instrument;
	}
	
}
