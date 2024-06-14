package artmcm.common;
import java.util.Arrays;
import java.util.List;
import combinatorial.CTModel;
import combinatorial.TestCase;

public class CaseSIR extends common.CaseSIR implements Case
{

	public List<String> faults;
    public CaseSIR(String name) {
    	super(name);
    }
    
    @Override
    public boolean detected(TestCase tc) {
		int[] at = tc.test;
        for (final String faultStr : faults) {
        	Fault fault = mapFault.get(faultStr);
            if (this.hitting(at, fault)) {
               return true;
            }
        }
        return false;
    }
    
    private boolean hitting(int[] test, Fault fault) {
        for (int[] fs : fault.set) {
            if (Arrays.equals(fs, test)) {
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
