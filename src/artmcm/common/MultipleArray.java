package artmcm.common;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


/**
 * MultipleArray
 *
 * @param <T> 
 */
public class MultipleArray <T>{
	public static long time = 0;
	
	private int dimension;
	
	private Object array;
	

	public MultipleArray(T init,int... lengths) {
		super();
		this.dimension = lengths.length;
		array = Array.newInstance(init.getClass(), lengths);
		initArray(init,array,0); 
	}
	
	public MultipleArray(Class<T> type,int... lengths) {
		super();
		this.dimension = lengths.length;
		array = Array.newInstance(type, lengths);
	}
	
	private T[] findItem(int[] paramIndex, int[] x,int... indexes) {
		Object object = array;
		for (int i = 0; i < indexes.length-1; i++) {
			int index = indexes[i];
			if(x[index] == -1) {
				return null;
			}
			object = ((Object[])object)[paramIndex[index]+ x[index]];
		}
		return (T[])object;
	}
	
	private T[][] findItem(int[] x,int... indexes) {
		Object object = array;
		for (int i = 0; i < indexes.length-1; i++) {
			int index = indexes[i];
			object = ((Object[][])object)[index][x[index]];
		}
		return (T[][])object;
	}
	
	public void initArray(T init,Object array,int depth) {
		if(depth == dimension - 1) {
			Arrays.fill((T[]) array, init);
			return;
		}
		int length = Array.getLength(array); 
		for(int i = 0 ; i < length; i ++) {
			initArray(init, Array.get(array, i), depth + 1);
		}
	}
	
	
	
	public T getObject(int[] paramIndex,int[] x,int[] indexes) {
		T[] item = findItem(paramIndex,x,indexes);
		if(item == null) {
			return null;
		}
		int end = indexes[indexes.length-1];
		int index = paramIndex[end] + x[end];
		return item[index];
	}
	
	
	public T getObjectByPC(int[] paramIndex,int[] fpc,int[] lpc) {
		Object object = array;
		int i = 0;
		int index = 0;
		for (; i < fpc.length; i++) {
			index = paramIndex[fpc[i]] + lpc[i];
			object = ((Object[])object)[index];
		}
		return (T) object;
	}
	
	
	public void setObject(int[] paramIndex,int[] x,T value,int... indexes) {
		T[] item = findItem(paramIndex,x,indexes);
		int end = indexes[indexes.length-1];
		item[paramIndex[end]+x[end]] = value;
	}
	
	public Integer getAndIncrease(int[] paramIndex, int[] x, int... indexes) throws Exception {
		T[] item = findItem(paramIndex,x,indexes);
		int end = indexes[indexes.length-1];
		int index = paramIndex[end] +x[end];
		Object value = item[index]; 
		if(value instanceof Integer) {
			value = ((Integer)value) + 1;
			item[index] = (T) value;
			return ((Integer) value);
		}
		else
			throw new Exception();
	}
	
	public Integer getAndIncrease(int[] x, int... indexes) throws Exception {
		T[][] item = findItem(x,indexes);
		int end = indexes[indexes.length-1];
		Object value = item[end][x[end]]; 
		if(value instanceof Integer) {
			value = ((Integer)value) + 1;
			item[end][x[end]] = (T) value;
			return ((Integer) value);
		}
		else
			throw new Exception();
	}

	public Object getArray() {
		return array;
	}

	public void setArray(Object array) {
		this.array = array;
	}
	
}
