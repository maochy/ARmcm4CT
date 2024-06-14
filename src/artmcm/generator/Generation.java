package artmcm.generator;

import combinatorial.CTModel;
import artmcm.common.Case;
import combinatorial.TestSuite;

/**
 * @author: Linlin Wen
 * 
 *
 */
public interface Generation {
	
	public long time = 0;
	
	public void generation(CTModel model, TestSuite ts,Case scenario);
	
	public default void generation(CTModel model, TestSuite ts,int size) {}

	public default void generation(CTModel model, TestSuite ts) {}
	
	public default long getTime() {
		return time;
	}
	
}
