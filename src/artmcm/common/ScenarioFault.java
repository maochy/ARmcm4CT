package artmcm.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class ScenarioFault {
	
	public String[] faults;
	public double rate;
	public ScenarioFault(String[] faults, double rate) {
		this.faults = faults;
		this.rate = rate;
	}
	
	public static List<ScenarioFault> getScenarioFault(Scanner scanner){
    	ArrayList<ScenarioFault> allScenario = new ArrayList<>();
    	if(scanner.hasNextLine()) {
    		scanner.nextLine();
    		double[] rates = Stream.of(scanner.nextLine().substring(5).replaceAll("\\[|\\]|\\ ", "").split(","))
    				.mapToDouble(e->Double.parseDouble(e)).toArray();
    		for (int i = 0; i < rates.length; i++) {
				String[] faults = scanner.nextLine().replaceAll("\\[|\\]|\\ ", "").split(",");
				allScenario.add(new ScenarioFault(faults, rates[i]));
			}
    	}
    	scanner.nextLine();
    	return allScenario;
    }

}
