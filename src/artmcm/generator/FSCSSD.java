package artmcm.generator;

import java.util.Random;

import combinatorial.CTModel;
import artmcm.common.Case;
import combinatorial.TestCase;
import combinatorial.TestSuite;
import generator.RandomSample;

/**
 * @author: Linlin Wen
 *	FSCS-SD
 */
public class FSCSSD implements Generation
{
    private CTModel model;
    private Random random;
    private int CANDIDATE;
    
    public FSCSSD() {
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
             TestCase tc = new TestCase(this.nextTestCase(ts, ts.getTestSuiteSize(), this.CANDIDATE,false));
            ts.suite.add(tc);
            model.updateCombination(tc.test);
        }
         long endTime = System.nanoTime();
        ts.time = endTime - startTime;
    }



    public void generation( CTModel model,  TestSuite ts, int size) {
    	(this.model = model).initialization();
         long startTime = System.nanoTime();
        ts.suite.clear();
         int[] test = RandomSample.sample(model, this.random);
        ts.suite.add(new TestCase(test));
        while (ts.getTestSuiteSize() != size) {
             TestCase tc = new TestCase(this.nextTestCase(ts, ts.getTestSuiteSize(), this.CANDIDATE,false));
            ts.suite.add(tc);
        }
         long endTime = System.nanoTime();
        ts.time = endTime - startTime;
    }
    
    public void generation(CTModel model, TestSuite ts,Case scenario) {
        (this.model = model).initialization();
        ts.suite.clear();
         int[] test = RandomSample.sample(model, this.random);
        TestCase testCase = new TestCase(test); 
        ts.suite.add(testCase);
        model.updateCombination(test);
        if (scenario.detected(testCase)) {
			return;
		}
        int count = 1;
        while (true) {
        	  TestCase tc = new TestCase(this.nextTestCase(ts, count++, this.CANDIDATE,true));
             ts.suite.add(tc);
             if(scenario.detected(tc)) {
             	return;
             }
            model.updateCombination(tc.test);
		}
    }
    
    private int[] nextTestCase( TestSuite ts,  int index,  int k,boolean muti) {
         int[][] candidate = new int[k][];
        int[] tp;
        for (int pc = 0; pc < k; candidate[pc++] = tp) {
            tp = RandomSample.sample(this.model, this.random);
        }
        long[] fitness = new long[candidate.length];
        if (muti){
            Thread[] threads = new Thread[candidate.length];
            for (int i = 0; i < candidate.length; ++i) {
                threads[i] = new Thread(()->{
                    int ti = Integer.parseInt(Thread.currentThread().getName());
                    fitness[ti] = this.model.fitnessValue(candidate[ti]);
                },i+"");
                threads[i].start();
            }
            for (int i = 0; i < candidate.length; ++i) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            for (int i = 0; i < candidate.length; ++i) {
                fitness[i] = this.model.fitnessValue(candidate[i]);
            }
        }

        int maxIndex = 0;
        double maxFit = fitness[0];
        for (int m = 1; m < fitness.length; ++m) {
            if (fitness[m] > maxFit) {
                maxIndex = m;
                maxFit = fitness[m];
            }
        }
        return candidate[maxIndex];
    }

    
}
