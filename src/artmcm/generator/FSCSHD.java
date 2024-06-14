package artmcm.generator;

import java.util.*;

import combinatorial.CTModel;
import artmcm.common.Case;
import combinatorial.*;
import generator.ART;
import generator.RandomSample;

/**
 * @author: Linlin Wen
 *
 *	FSCS-HD
 */
public class FSCSHD extends ART implements Generation
{
    private CTModel model;
    private Random random;
    private int CANDIDATE;
    
    public FSCSHD() {
        this.CANDIDATE = 10;
        this.random = new Random();
    }
    
    public void setCANDIDATE( int candidate) {
        this.CANDIDATE = candidate;
    }
    
    public void generation( CTModel model,  TestSuite ts) {
         long startTime = System.nanoTime();
        (this.model = model).initialization();
		ts.suite.clear();
         int[] test = RandomSample.sample(model, this.random);
        ts.suite.add(new TestCase(test));
        model.updateCombination(test);
        while (model.getCombUncovered() != 0L) {
             TestCase tc = new TestCase(this.nextTestCase(ts, ts.getTestSuiteSize(), this.CANDIDATE));
            ts.suite.add(tc);
            model.updateCombination(tc.test);
        }
         long endTime = System.nanoTime();
        ts.time = endTime - startTime;
    }


    public void generation( CTModel model,  TestSuite ts, int size) {
    	super.generation(model, ts, size);
    }
    
    public void generation(CTModel model, TestSuite ts,Case scenario) {
        this.model = model;
        ts.suite.clear();
         int[] test = RandomSample.sample(model, this.random);
        TestCase testCase = new TestCase(test); 
        ts.suite.add(testCase);
        if (scenario.detected(testCase)) {
			return;
		}
        int count = 1;
        while (true) {
        	  TestCase tc = new TestCase(this.nextTestCase(ts, count++, this.CANDIDATE));
             ts.suite.add(tc);
             if(scenario.detected(tc)) {
             	return;
             }
		}
    }
    
    private int[] nextTestCase(TestSuite ts,  int index,  int size) {
    	int[] best = null;
	    int fitness = Integer.MIN_VALUE;
	    for (int pc = 0; pc < size; pc ++) {
	    	 int[] tp = RandomSample.sample(this.model, this.random);
	    	 int fit = Integer.MAX_VALUE;
	    	 for (int i = 0; i < index; i ++) {
	            int dist = this.distance(tp, ts.suite.get(i).test);
	            if (fit > dist) {
	            	fit = dist;
	            }
	         }
	    	 if (fitness < fit) {
	    		 fitness = fit;
	    		 best = tp;
	    	 }
	    }
	    return best;
    }
    
    private int distance(int[] x,  int[] y) {
        int dist = 0;
        for (int i = 0; i < x.length; ++i) {
            if (x[i] != y[i]) {
                ++dist;
            }
        }
        return dist;
    }
    
    
    
}
