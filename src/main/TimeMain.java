package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import artmcm.common.CaseSIR;
import artmcm.generator.AETG;
import artmcm.generator.ARTsum;
import artmcm.generator.ARmcmCT;
import artmcm.generator.FSCSHD;
import artmcm.generator.FSCSSD;
import artmcm.generator.Generation;
import artmcm.generator.RT;
import combinatorial.TestSuite;
import combinatorial.CTModel;
/**
 * @author: Linlin Wen
 *
 *时间实验主类
 *
 */
public class TimeMain {
	
	public static void run(int[] sizes,CTModel sub,int loop,String root,Class[] alg) throws Exception {
		for (int i = 0; i < alg.length; i++) {
			Class<?> algorithm = alg[i];
			Generation generation = (Generation) algorithm.getConstructor().newInstance();
			for (int j = 0; j < sizes.length; j++) {
				for (int k = 0; k < loop; k++) {
					TestSuite ts  = new TestSuite();
					generation.generation(sub, ts, sizes[j]);
					writeToFile(ts.getTestSuiteTime(),algorithm.getSimpleName(),sizes[j],root);
				}
			}
		}
	}
	
	private static void writeToFile(long testSuiteTime, String simpleName, int size,String root) throws IOException {
		File file = new File(root+"//"+simpleName+"//"+size+".txt");
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileWriter writer = new FileWriter(file,true);
		writer.append(testSuiteTime+"\r\n");
		writer.flush();
		writer.close();
	}
	

	public static void main(String[] args) throws Exception {
		Class[] alg = new Class[] {
				AETG.class,
				ARmcmCT.class,
        		RT.class,
        		ARTsum.class,
        		FSCSHD.class,
        		FSCSSD.class,
		};
		int[] sizes = {100,200,500,1000,2000,5000,10000};
		int loop = 10000;
		CaseSIR subject = new CaseSIR("make");
		CTModel sub = subject.getSubModel(5,0,2);
		run(sizes, sub, loop,"result/time_5p_2way",alg);
		sub = subject.getSubModel(10,0,2);
		run(sizes, sub, loop,"result/time_10p_2way",alg);
		
		alg = new Class[] {
				ARmcmCT.class,
		};
		sub = subject.getSubModel(5,0,3);
		run(sizes, sub, loop,"result/time_5p_3way",alg);
		sub = subject.getSubModel(10,0,3);
		run(sizes, sub, loop,"result/time_10p_3way",alg);
		
	}

}
