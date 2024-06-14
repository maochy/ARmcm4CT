package artmcm.generator;

import java.util.*;

import combinatorial.CTModel;
import artmcm.common.Case;
import combinatorial.*;
import generator.RandomSample;

/**
 * @author: Linlin Wen
 * 
 * ARTsum
 *
 */
public class ARTsum implements Generation
{
    private CTModel model;
    private Random random;
    private int CANDIDATE;
    private int[][] paraVector; 
    
    public ARTsum() {
        this.CANDIDATE = 10;
        this.random = new Random();
    }
    
    public void setCANDIDATE(int candidate) {
        this.CANDIDATE = candidate;
    }
    
    private void initParaVector(CTModel model) {
    	paraVector = new int[model.parameter][];
    	for (int i = 0; i < paraVector.length; i++) {
    		paraVector[i] = new int[model.value[i]];
		}
    }
    
    private void updateParaVector(int[] x) {
    	for (int i = 0; i < x.length; i++) {
			paraVector[i][x[i]]++;
		}
    }


    public void generation(CTModel model, TestSuite ts, int size) {
    	(this.model = model).initialization();
        final long startTime = System.nanoTime();
        initParaVector(model);
        ts.suite.clear();
        final int[] test = RandomSample.sample(model, this.random);
        ts.suite.add(new TestCase(test));
        updateParaVector(test);
        while (ts.getTestSuiteSize() != size) {
            TestCase tc = new TestCase(this.nextTestCase(ts, ts.getTestSuiteSize(), this.CANDIDATE));
            ts.suite.add(tc);
            updateParaVector(tc.test);
        }
        final long endTime = System.nanoTime();
        ts.time = endTime - startTime;
    }
    
    public void generation(CTModel model, TestSuite ts) {
        final long startTime = System.currentTimeMillis();
        this.model = model;
        model.initialization();
        initParaVector(model);
        ts.suite.clear();
        final int[] test = RandomSample.sample(model, this.random);
        ts.suite.add(new TestCase(test));
        updateParaVector(test);
        model.updateCombination(test);
        while (model.getCombUncovered() != 0L) {
            TestCase tc = new TestCase(this.nextTestCase(ts, ts.getTestSuiteSize(), this.CANDIDATE));
            ts.suite.add(tc);
            updateParaVector(tc.test);
            model.updateCombination(tc.test);
        }
        final long endTime = System.currentTimeMillis();
        ts.time = endTime - startTime;
    }
    
    public void generation(CTModel model, TestSuite ts,Case scenario) {
        this.model = model;
        ts.suite.clear();
        final int[] test = RandomSample.sample(model, this.random);
        TestCase testCase = new TestCase(test);
        ts.suite.add(new TestCase(test));
        if(scenario.detected(testCase)) {
        	return;
        }
        initParaVector(model);
        updateParaVector(test);
        int count = 1;
        while (true) {
        	TestCase tc = new TestCase(this.nextTestCase(ts, count, this.CANDIDATE));
            ts.suite.add(tc);
            if(scenario.detected(tc)) {
            	return;
            }
            updateParaVector(tc.test);
            count++;
		}
    }
    
    private int[] nextTestCase(TestSuite ts,int size, final int k) {
        final int[][] candidate = new int[k][];
        int[] tp;
        for (int pc = 0; pc < k; candidate[pc++] = tp) {
            tp = RandomSample.sample(this.model, this.random);
        }
        int index = 0;
        int maxFit = Integer.MIN_VALUE;
        for (int i = 0; i < candidate.length; i ++) {
        	int distance = distance(candidate[i], size);
        	if(distance > maxFit) {
        		index = i;
        		maxFit = distance;
        	}
        }
        return candidate[index];
    }
    
    private int distance(final int[] x,int size) {
        int dist = 0;
        for (int i = 0; i < x.length; i++) {
			dist += (size-paraVector[i][x[i]]);
		}
        return dist;
    }
}
