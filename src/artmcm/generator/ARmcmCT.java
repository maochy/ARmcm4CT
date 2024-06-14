package artmcm.generator;

import combinatorial.TestCase;
import combinatorial.TestSuite;
import generator.RandomSample;
import java.util.ArrayList;
import java.util.Random;

import combinatorial.CTModel;
import artmcm.common.Case;
import artmcm.common.MultipleArray;

/**
 * @author: Linlin Wen
 *
 * ARmcm-CT
 *
 */
public class ARmcmCT implements Generation
{
    private CTModel model;
    private Random random;
    private int CANDIDATE;
    private MultipleArray<Integer> paraVector;
    private ArrayList<int[]> pairs;
    private long unCover = 0;
    private int[] paramIndex;

    public ARmcmCT() {
        this.CANDIDATE = 10;
        this.random = new Random();
    }
    
    public void setCANDIDATE(int candidate) {
        this.CANDIDATE = candidate;
    }
    
    private void initParaVector(CTModel model) {
    	paramIndex = new int[model.parameter+1];
    	for (int i = 0; i < paramIndex.length-1; i++) {
    		paramIndex[i + 1] = paramIndex[i] + model.value[i];
		}
    	int[] length = new int[model.t_way];
    	for (int i = 0; i < length.length; i ++) {
    		length[i] = paramIndex[model.parameter];
		}
    	paraVector = new MultipleArray<Integer>(0,length);
    	pairs = new ArrayList<>();
    	pairs = new ArrayList<>();
    	unCover = getPairs();
    }
    
    private long getPairs() {
    	int[] starts = new int[model.t_way];
    	int depth = 0;
    	int[] pair = new int[model.t_way];
    	long totalPC = 0;
    	while(starts[0] <= model.parameter - model.t_way) {
    		if(depth ==  model.t_way - 1) {
    			for(;starts[depth] < model.parameter;starts[depth] ++) {
    				pair[depth] = starts[depth];
    				int[] temp = new int[pair.length];
    				int count = 1;
    				for (int i = 0; i < temp.length; i++) {
    					temp[i] = pair[i];
    					count *= model.value[pair[i]];
					}
    				totalPC += count;
    				pairs.add(temp);
    			}
    			starts[depth-1] ++;
    			depth --;
    			continue;
    		}
    		if(starts[depth] < model.parameter) {
    			pair[depth] = starts[depth];
    			depth ++;
    			starts[depth] = starts[depth-1] +1;
    			continue;
    		}
    		else {
    			starts[depth-1] ++;
    			depth --;
    		}
    	}
    	return totalPC;
    }
    
    private void updateParaVector(int[] x) {
    	for (int i = 0; i < pairs.size(); i++) {
    		int[] pair = pairs.get(i);
    		try {
				int val = paraVector.getAndIncrease(paramIndex,x,pair);
				if(val == 1) {
					unCover --;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
        while(ts.getTestSuiteSize() != size) {
            final TestCase tc = new TestCase(this.nextTestCase(ts, ts.getTestSuiteSize(), this.CANDIDATE,false));
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
//        model.updateCombination(test);
        while(unCover != 0L) {
            final TestCase tc = new TestCase(this.nextTestCase(ts, ts.getTestSuiteSize(), this.CANDIDATE,false));
            ts.suite.add(tc);
            updateParaVector(tc.test);
//            model.updateCombination(tc.test);
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
        	TestCase tc = new TestCase(this.nextTestCase(ts, count, this.CANDIDATE,true));
            ts.suite.add(tc);
            if(scenario.detected(tc)) {
            	return;
            }
            updateParaVector(tc.test);
			count++;
		}
    }

    private int[] nextTestCase(TestSuite ts,int size, final int k,boolean multi) {
        final int[][] candidate = new int[k][];
        int[] tp;
        for (int pc = 0; pc < k; candidate[pc++] = tp) {
            tp = RandomSample.sample(this.model, this.random);
        }
        double[] dist = new double[CANDIDATE];
        if(multi){
            Thread[] threads = new Thread[candidate.length];
            for (int i = 0; i < candidate.length; i++) {
                threads[i] = new Thread(()->{
                    int ti = Integer.parseInt(Thread.currentThread().getName());
                    dist[ti] = distance(candidate[ti],size);
                },i+"");
                threads[i].start();
            }
            for (int i = 0; i < candidate.length; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        int index = 0;
        double maxFit = Double.MIN_VALUE;
        for (int i = 0; i < candidate.length; i ++) {
            double distance = multi?dist[i]:distance(candidate[i], size);
            if(distance > maxFit) {
                index = i;
                maxFit = distance;
            }
        }
        return candidate[index];
    }
    
    private double distance(final int[] x,int size) {
        double dist = 0;
        for (int[] pair : pairs) {
        	int temp = paraVector.getObject(paramIndex,x,pair);
			dist += 1.0/(1.0+temp)*(size-temp);
		}
        return dist;
    }
}
