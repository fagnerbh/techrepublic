package techrepublic.pricehistory.entity;

public interface CandleStickInterface extends Cloneable {
	
	public void registerNewQuote(Quote quote);
	
	public void close();

}
