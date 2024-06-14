package artmcm.generator;

import java.util.*;
import java.util.stream.Collectors;

import artmcm.common.AETGModel;
import artmcm.common.Case;
import generator.RandomSample;
import combinatorial.CTModel;
import combinatorial.TestCase;
import combinatorial.TestSuite;
import common.ALG;

public class AETG  implements Generation{
	public  AETGModel model;
	private int CANDIDATE;
	private Random random;
			
	public AETG() {
		this.CANDIDATE = 10;
		this.model = null;
		this.random = new Random();
	}

	public void setCANDIDATE( int candidate) {
		this.CANDIDATE = candidate;
	}
	
	public void generation(CTModel model,  TestSuite ts,int size) {
    	(this.model = new AETGModel(model.parameter,model.value,model.t_way)).initialization();
        long startTime = System.nanoTime();
        this.model.initLevelCover();
        ts.suite.clear();
        while (ts.getTestSuiteSize() != size) {
        	int[] next = this.nextBestTestCase(this.CANDIDATE);
        	TestCase testCase = null;
            if (next == null) {
            	testCase = new TestCase(RandomSample.sample(model, random));
			}
            else {
            	testCase = new TestCase(next);
			}
            ts.suite.add(testCase);
            this.model.updateCombinationAndLevel(testCase.test);
        }
        long endTime = System.nanoTime();
        ts.time = endTime - startTime;
    }

	public void generation(CTModel model, TestSuite ts) {
		(this.model = new AETGModel(model.parameter,model.value,model.t_way)).initialization();
		this.model.initLevelCover();
		ts.suite.clear();
		while (this.model.getCombUncovered() != 0L) {
			 int[] next = this.nextBestTestCase(this.CANDIDATE);
			if (next == null) {
				break;
			}
			TestCase best = new TestCase(next);
			ts.suite.add(best);
			this.model.updateCombinationAndLevel(best.test);
		}
	}
	
	public void generation(CTModel model, TestSuite ts,Case scenario) {
		(this.model = new AETGModel(model.parameter,model.value,model.t_way)).initialization();
		ts.suite.clear();
		this.model.initLevelCover();
		while (this.model.getCombUncovered() != 0L) {
			if(this.model.combination == null) {
				System.out.println("null");
			}
			 int[] next = this.nextBestTestCase(this.CANDIDATE);
			if (next == null) {
				break;
			}
			 TestCase best = new TestCase(next);
			ts.suite.add(best);
			if(scenario.detected(best)) {
				return;
			}
			this.model.updateCombinationAndLevel(best.test);
		}
		while (true) {
			TestCase testCase = new TestCase(RandomSample.sample(model, random));
			ts.suite.add(testCase);
			if(scenario.detected(testCase)) {
				return;
			}
		}
	}
	
	public int[] nextBestTestCase( int N) {
		int[] tp;
		int[][] candidate = new int[N][model.parameter];
		for (int pc = 0; pc < N; pc ++) {
			tp = this.nextTestCase();
			candidate[pc] = tp;
		}
		long[] fitness = new long[N];
		for (int i = 0; i < N; i++) {
			fitness[i] = model.fitnessValue(candidate[i]);
		}
		long covBest = fitness[0];
		int index = 0;
		for (int x = 1; x < N; ++x) {
			long covTemp = fitness[x];
			if (covTemp == this.model.getTestCaseCoverMax()) {
				return candidate[x];
			}
			if (covTemp > covBest) {
				index = x;
				covBest = covTemp;
			}
		}
		return candidate[index];
	}
	
	private int[] nextTestCase() {
		int[] maxCover = model.getMaxPar();
		int[] tc = new int[model.parameter];
		for (int i = 0; i < tc.length; i++) {
			if(i == maxCover[0]) {
				tc[i] = maxCover[1];
			}
			tc[i] = -1;
		}
		 List<Integer> permutation = new ArrayList<Integer>();
		for (int k = 0; k < this.model.parameter; ++k) {
			if (tc[k] == -1) {
				permutation.add(k);
			}
		}
		Collections.shuffle(permutation);
		for ( int par : permutation) {
			tc[par] = this.selectBestValue(tc, par);
		}
		return tc;
	}
	
	private int selectBestValue( int[] test,  int par) {
		 ArrayList<Pair> vs = new ArrayList<Pair>();
		for (int i = 0; i < this.model.value[par]; ++i) {
			 int num = this.coveredSchemaNumberFast(test, par, i);
			vs.add(new Pair(i, num));
		}
		Collections.sort(vs);
		 int max = vs.get(0).number;
		 List<Pair> filtered = vs.stream().filter(p -> p.number == max).collect(Collectors.toList());
		 int r = this.random.nextInt(filtered.size());
		return filtered.get(r).index;
	}
	
	private int coveredSchemaNumberFast( int[] test,  int par,  int val) {
		int fit = 0;
		int count = 0;
		 int[] new_test = new int[this.model.parameter];
		for (int i = 0; i < this.model.parameter; ++i) {
			new_test[i] = test[i];
			if (test[i] != -1) {
				++count;
			}
		}
		
		new_test[par] = val;
		 int assigned = count; 
		 int required = this.model.t_way - 1;
		
		 int[] fp = new int[assigned];
		 int[] fv = new int[assigned];
		int j = 0;
		int k = 0;
		while (j < this.model.parameter) {
			if (new_test[j] != -1 && j != par) {
				fp[k] = j;
				fv[k++] = new_test[j];
			}
			++j;
		}
		 int[] pp = { par };
		 int[] vv = { val };
		for ( int[] each : ALG.allCombination(assigned, required)) {
			 int[] pos = new int[required];
			 int[] sch = new int[required];
			for (int l = 0; l < required; ++l) {
				pos[l] = fp[each[l]];
				sch[l] = fv[each[l]];
			}
			 int[] position = new int[this.model.t_way];
			 int[] schema = new int[this.model.t_way];
			mergeArray(pos, sch, pp, vv, position, schema);
			if (!this.model.covered(position, schema, 0)) {
				++fit;
			}
		}
		return fit;
	}
	
	private static void mergeArray( int[] p1,  int[] v1,  int[] p2,  int[] v2,  int[] pos,
			 int[] sch) {
		int i = 0;
		int j = 0;
		int k = 0;
		while (i < p1.length && j < p2.length) {
			if (p1[i] < p2[j]) {
				pos[k] = p1[i];
				sch[k++] = v1[i++];
			} else {
				pos[k] = p2[j];
				sch[k++] = v2[j++];
			}
		}
		if (i < p1.length) {
			while (i < p1.length) {
				pos[k] = p1[i];
				sch[k] = v1[i];
				++i;
				++k;
			}
		}
		if (j < p2.length) {
			while (j < p2.length) {
				pos[k] = p2[j];
				sch[k] = v2[j];
				++j;
				++k;
			}
		}
	}
	
	private class Pair implements Comparable<Pair> {
		public int index;
		public int number;

		private Pair( int i,  int n) {
			this.index = i;
			this.number = n;
		}

		@Override
		public int compareTo(Pair B) {
			return -Integer.compare(this.number, B.number);
		}

		@Override
		public String toString() {
			return String.valueOf(this.index) + " (" + String.valueOf(this.number) + ")";
		}
	}

	

}
