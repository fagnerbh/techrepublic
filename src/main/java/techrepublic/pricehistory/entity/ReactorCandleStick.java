package techrepublic.pricehistory.entity;

import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;
import reactor.core.publisher.Mono;

@Getter
public class ReactorCandleStick extends CandleStick {

	private final TimerTask closeTask;
	private Mono<ReactorCandleStick> closedPublisher;
	
	public ReactorCandleStick(Quote quote) {
		super(quote);	
		
		closeTask = new TimerTask() {
	        public void run() {
	            close();
	        }
	    };
	    
	    Timer timer = new Timer("Timer-" + quote.getData().getIsin());
	    
	    long delay = 1000L;
	    timer.schedule(closeTask, delay);
	    
	    closedPublisher = Mono.empty();
	}
	
	public ReactorCandleStick(Quote quote, long closeDelay) {
		super(quote);	
		
		closeTask = new TimerTask() {
	        public void run() {
	            close();
	        }
	    };
	    
	    Timer timer = new Timer("Timer-" + quote.getData().getIsin());
	    
	    long delay = closeDelay;
	    timer.schedule(closeTask, delay);
	    
	    closedPublisher = Mono.empty();
	}
	
	
	public ReactorCandleStick(String isin) {
		super(isin) ;
		
		closeTask = new TimerTask() {
	        public void run() {
	            close();
	        }
	    };
	    
	    Timer timer = new Timer("Timer-" + isin);
	    
	    long delay = 1000L;
	    timer.schedule(closeTask, delay);
	    
	    closedPublisher = Mono.empty();
	}
	
	public ReactorCandleStick(String isin, long closeDelay) {
		super(isin) ;
		
		closeTask = new TimerTask() {
	        public void run() {
	            close();
	        }
	    };
	    
	    Timer timer = new Timer("Timer-" + isin);
	    
	    long delay = closeDelay;
	    timer.schedule(closeTask, delay);
	    
	    closedPublisher = Mono.empty();
	}

	@Override
	public void close() {		
		super.close();
		closedPublisher = Mono.defer(() -> Mono.just(this)); //TODO: create a new inner class inside WindowViewManager to deal with subscriptions. 
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return (ReactorCandleStick) super.clone();
	}	
	
}
