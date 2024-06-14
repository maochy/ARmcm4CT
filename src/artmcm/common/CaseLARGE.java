package artmcm.common;

import java.util.*;
import combinatorial.CTModel;
import combinatorial.TestCase;

public class CaseLARGE extends common.CaseLARGE implements Case
{
	public List<String> faults;
    
    public CaseLARGE(String name) {
        super(name);
    }
    
    
    @Override
    public boolean detected(TestCase tc) {
    	int[] at = this.amend(tc.test);
        for (String faultStr : faults) {
        	Fault fault = mapFault.get(faultStr);
            if (this.hitting(at, fault)) {
            	return true;
            }
        }
    	return false;
    }
    
	@Override
	public void setFaultsList(List<String> faults) {
		this.faults = faults;
	}


	@Override
	public CTModel getSubModel(int i, int j, int k) {
		return super.getSubModel(i, j, k);
	}

}
