package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import combinatorial.CTModel;
import artmcm.common.Case;
import artmcm.common.CaseLARGE;
import artmcm.common.CaseSIR;
import artmcm.common.ScenarioFault;
import artmcm.generator.AETG;
import artmcm.generator.ARTsum;
import artmcm.generator.ARmcmCT;
import artmcm.generator.FSCSHD;
import artmcm.generator.FSCSSD;
import artmcm.generator.Generation;
import artmcm.generator.RT;
import combinatorial.TestSuite;

/**
 * @author: Linlin Wen
 * evaluation: CAT-measure，F-measure
 *
 */
public class Main {
	
	public static void main(String[] args) throws Exception {
		int[] strength = {2,3,4};
		int[] sirPara = {
				9,
				9,
				14,
				11,
				10,
				7
		};
		int[] largePara = {
				68,
        		47,
				104
		};
		String[] sirNames = {
				"flex",
				"grep",
				"gzip",
				"sed",
				"make",
				"nanoxml",
		};
		String[] largeNames = {
				"busybox",
        		"drupal",
				"linux"
		};
		Class[] alg = new Class[] {
				AETG.class,
				ARmcmCT.class,
        		RT.class,
        		ARTsum.class,
        		FSCSHD.class,
        		FSCSSD.class,
		};
		int[] slist = {2,8,3,18,0,0}; //the index of sir scenario
		int[] llist = {0,0,0}; //the index of large scenario
		runCM(strength,sirNames,sirPara,alg,CaseSIR.class,10000);
		runCM(strength,largeNames,largePara,alg,CaseLARGE.class,1000);
		runFM(strength, sirNames, sirPara, alg, CaseSIR.class, slist, 10000);
		runFM(strength, largeNames, largePara, alg, CaseLARGE.class, llist, 1000);
	}


	private static void runCM(int[] strength,String[] names, int[] paras, Class[] alg,Class caseClass,int size) throws Exception {
		for (int i = 0; i < alg.length; i++) {
			evaluationCM(strength, names, paras, caseClass,alg[i],size);
		}
	}
	
	private static void runFM(int[] strength,String[] names, int[] paras, Class[] algs,Class caseClass,int[] slist,int size) throws Exception {
		for (int i = 0; i < algs.length; i++) {
			evaluationFM(strength, names, paras, caseClass,algs[i],slist,size); //FM测试
		}
	}

	
	private static void evaluationCM(int[] strength,String[] names,int[] paras,Class caseClass, Class<?> algorithm,int size) {
		for (int i = 0; i < strength.length; i++) {
			for (int j = 0; j < names.length; j++) {
				Case subject = null;
				File scenarioFile = null;
				try {
					subject = (Case) caseClass.getConstructor(String.class).newInstance(names[j]);
					scenarioFile = new File("scenario/"+names[j]+".scenario");
					CTModel sub = subject.getSubModel(paras[j],0,strength[i]);
					Generation generation = (Generation) algorithm.getConstructor().newInstance();
					for(int count=0; count<size; count ++) {
						TestSuite ts  = new TestSuite();
			        	generation.generation(sub, ts);
			        	writeToFile("result/CAT_measure/",strength[i], algorithm.getSimpleName(), names[j],
			        			paras[j], 0, 0, ts.getTestSuiteSize());
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
	private static void evaluationFM(int[] strength,String[] names,int[] paras,Class caseClass, Class<?> algorithm,int[] slist,int size) {
		for (int i = 0; i < strength.length; i++) {
			for (int j = 0; j < names.length; j++) {
				Case subject = null;
				File scenarioFile = null;
				List<ScenarioFault> faults = null;
				try {
					subject = (Case) caseClass.getConstructor(String.class).newInstance(names[j]);
					scenarioFile = new File("scenario/"+names[j]+".scenario");
	                Scanner sScanner = new Scanner(scenarioFile);
					faults = ScenarioFault.getScenarioFault(sScanner);
					ScenarioFault scenarioFault = faults.get(slist[j]);
					List<String> faultList = Arrays.asList(scenarioFault.faults);
					subject.setFaultsList(faultList);
					CTModel sub = subject.getSubModel(paras[j],0,strength[i]);
					Generation generation = (Generation) algorithm.getConstructor().newInstance();
					for(int count=0; count<size; count ++) {
						TestSuite ts  = new TestSuite();
						generation.generation(sub, ts ,subject);
						writeToFile("result/F_measure/",strength[i], algorithm.getSimpleName(), names[j],
		    					paras[j], 0, -1, ts.getTestSuiteSize());
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void writeToFile(String dir,int strength,String algorithm,String name,int para,double cons,int num,Object value) {
		 File file = null;
		 if(num == -1) {
			 file =new File(dir+"/"+strength+"/"+algorithm+"/"+name+"/"+para+"_"+cons+".txt");	
		 }else{
			 file =new File(dir+"/"+strength+"/"+algorithm+"/"+name+"/"+para+"_"+cons+"_"+num+".txt");
		 }
   	if(!file.getParentFile().exists()) {
   		file.getParentFile().mkdirs();
   	}
		try {
			FileWriter writer = new FileWriter(file,true);
			writer.write(value+"\r\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }

}
