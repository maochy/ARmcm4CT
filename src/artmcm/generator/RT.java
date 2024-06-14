package artmcm.generator;

import java.util.*;

import combinatorial.CTModel;
import artmcm.common.Case;
import combinatorial.TestCase;
import combinatorial.TestSuite;
import generator.RandomSample;

public class RT implements Generation
{
    private CTModel model;
    private Random random;
    
    public RT() {
        this.random = new Random();
    }
    
    public void generation( CTModel model, TestSuite ts) {
        long startTime = System.currentTimeMillis();
        (this.model = model).initialization();
		ts.suite.clear();
        while (model.getCombUncovered() != 0L) {
            int[] tc = RandomSample.sample(model, this.random);
            ts.suite.add(new TestCase(tc));
            model.updateCombination(tc);
        }
        long endTime = System.currentTimeMillis();
        ts.time = endTime - startTime;
    }
    
    public void generation( CTModel model,  TestSuite ts,int size) {
         long startTime = System.nanoTime();
        while (ts.getTestSuiteSize() != size) {
            int[] tc = RandomSample.sample(model, this.random);
            ts.suite.add(new TestCase(tc));
        }
        long endTime = System.nanoTime();
        ts.time = endTime - startTime;
    }
    
    public void generation(CTModel model, TestSuite ts,Case scenario) {
    	this.model = model;
        ts.suite.clear();
        while(true) {
        	int[] tc = RandomSample.sample(model, this.random);
        	TestCase testCase = new TestCase(tc);
            ts.suite.add(testCase);
            if(scenario.detected(testCase)) {
            	return;
            }
        }
    }
}
