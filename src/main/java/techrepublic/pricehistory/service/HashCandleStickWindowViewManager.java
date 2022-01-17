package techrepublic.pricehistory.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import techrepublic.pricehistory.entity.CandleStick;
import techrepublic.pricehistory.entity.Instrument;
import techrepublic.pricehistory.entity.InstrumentTypeEnum;

/**
 * class implementation of CandleStickWindowViewManager which holds each candlestick window
 * as a queue of maximum size equals to the configurable candlesticks interval showed to the client.
 *  A candle stick window are all candle sticks for the last time interval in minutes, configurable
 *  in properties files.
 *  
 *  Public methods in this class are synchronized due to more time needed for its operations and less
 *  frequent updates
 *  
 */
@Service
@Qualifier("hashCandleStickWindowViewManager")
public class HashCandleStickWindowViewManager implements CandleStickWindowViewManager {
	
	@Value("${max.candlestick.window.view.size}")
	int maxWindowSize;
	
	protected static final Map<String, ConcurrentLinkedDeque<CandleStick>> candleWindows;
	
	static {
		candleWindows = new HashMap<String, ConcurrentLinkedDeque<CandleStick>>();
	}

	/**
	 * updates all available instruments' candlestick views. If an existing instrument view
	 * nas no update coming in map parameter, a copy of the last candle stick in the view queue
	 * is insert in this same queue.
	 * 
	 * the method is synchronized to prevent any instrument delete command while the views are being
	 * updated.
	 */
	@Override
	public synchronized void updateWindows(Map<String, CandleStick> mapCandle) {
		if ((mapCandle == null || mapCandle.isEmpty()) && candleWindows.isEmpty()) {
			return;
		}
		
		updateEachWindow(mapCandle);

	}

	@Override
	public synchronized Map<String, ConcurrentLinkedDeque<CandleStick>> getWindows() {
		return new HashMap<>(candleWindows);
	}
	
	@Override
	public synchronized void resetWindows() {
		candleWindows.clear();		
	}
	
	/**
	 * updates an Instrument data in the Window view manager. If the instrument in the parameter
	 * is of an ADD type, the method will check whether there is already a window view for the instrument's ISIN.
	 * If don't, creates a new window view for this ISIN. If the instrument in the parameter is of a DELETE type, 
	 * removes the correspondent ISIN window view from map of views.
	 */
	@Override
	public synchronized void update(Instrument instrument) {
		Optional.ofNullable(instrument).ifPresent((inst) -> {
			String isinToUpdate = inst.getData().getIsin();
			
			if (!candleWindows.containsKey(isinToUpdate) && inst.getType().equals(InstrumentTypeEnum.ADD.getType())) {
				candleWindows.put(isinToUpdate, new ConcurrentLinkedDeque<>());
			} else if (inst.getType().equals(InstrumentTypeEnum.DELETE.getType())) {
				candleWindows.remove(isinToUpdate);
			}
		});
		
	}
	
	/**
	 * returns this ISIN's window view in the format of a Collection, more suitable to construct the 
	 * final view for the final user.
	 */
	@Override
	public synchronized Collection<CandleStick> getWindowView(String isin) {		
		return candleWindows.get(isin);
	}
	
	/**
	 * updates a single window view, if there is an ISIN window view at the moment.
	 */
	@Override
	public synchronized void updateWindow(CandleStick candleStick) {
		Optional.ofNullable(candleWindows.get(candleStick.getIsin())).ifPresent((queue) -> {checkMaxCapacity(queue); queue.addLast(candleStick);});		
	}
	
	/**
	 * loop through the candle windows, updating them with a correspondent new candlestick
	 * present in Map of the method's parameter
	 * @param mapCandle - map of new candlesticks received
	 */
	protected void updateEachWindow(Map<String, CandleStick> mapCandle) {
		Set<String> isinKeys = candleWindows.keySet();
		
		if (mapCandle == null || mapCandle.isEmpty()) {
			for (String isin: isinKeys) {
				insertLastMinuteCandleAtEnd(candleWindows.get(isin));
			}
		} else {
			Set<String> mapCandleKeys = mapCandle.keySet();
			
			Set<String> notReceivedIsins = new HashSet<>(isinKeys);
			for (String mapKey: mapCandleKeys) {				
				if (isinKeys.contains(mapKey)) {
					checkMaxCapacity(candleWindows.get(mapKey));
					candleWindows.get(mapKey).addLast(mapCandle.get(mapKey));
					
					notReceivedIsins.remove(mapKey);
				} 
			}
			
			// for those remained in notReceivedIsins, they are the ones in the window view, but with no update.
			// so repeat the last candle stick for them.
			for(String isin: notReceivedIsins) {				
				insertLastMinuteCandleAtEnd(candleWindows.get(isin));
			}
		}
		
	}

	/**
	 * inserts a copy of the last candlestick in queue window in itself.
	 * @param concurrentLinkedDeque
	 */
	protected void insertLastMinuteCandleAtEnd(ConcurrentLinkedDeque<CandleStick> concurrentLinkedDeque) {
		try {
			checkMaxCapacity(concurrentLinkedDeque);
			concurrentLinkedDeque.addLast((CandleStick) concurrentLinkedDeque.peekLast().clone());
		} catch (CloneNotSupportedException e) {
			return;
		}		
	}
	
	/**
	 * check if the window view has reached the max capacitiy. If so, remove the head of the view.
	 * @param concurrentLinkedDeque
	 */
	protected void checkMaxCapacity(ConcurrentLinkedDeque<CandleStick> concurrentLinkedDeque) {
		if (concurrentLinkedDeque != null && concurrentLinkedDeque.size() == maxWindowSize) {
			concurrentLinkedDeque.pollFirst();
		}
	}		
}
