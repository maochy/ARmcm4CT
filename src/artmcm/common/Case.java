package artmcm.common;

import java.util.*;
import combinatorial.CTModel;
import combinatorial.TestCase;

public interface Case
{
	boolean detected(TestCase tc);

	void setFaultsList(List<String> faults);

	CTModel getSubModel(int i, int j, int k);

	
}
