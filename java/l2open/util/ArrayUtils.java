package l2open.util;

import java.lang.reflect.Array;
import java.util.Collection;

public final class ArrayUtils
{
	public static final int INDEX_NOT_FOUND = -1;

	public static <Object> Object valid(Object[] array, int index)
	{
		if(array == null)
			return null;
		if(index < 0 || array.length <= index)
			return null;
		return array[index];
	}

	@SuppressWarnings({"unchecked", "hiding"})
	public static <Object> Object[] add(Object[] array, Object element)
	{
		Class type = (element != null) ? element.getClass() : (array != null) ? array.getClass().getComponentType() : java.lang.Object.class;
		Object[] newArray = (Object[])copyArrayGrow(array, type);
		newArray[newArray.length - 1] = element;
		return newArray;
	}

	public static long[] add(long[] arr, long o)
	{
		if(arr == null)
		{
			arr = new long[1];
			arr[0] = o;
			return arr;
		}
		int len = arr.length;
		long[] tmp = new long[len+1];
		System.arraycopy(arr, 0, tmp, 0, len);
		tmp[len] = o;
		return tmp;
	}

	@SuppressWarnings({"unchecked", "unused", "hiding"})
	private static <Object> Object[] copyArrayGrow(Object[] array, Class<Object> type)
	{
		if (array != null)
		{
			int arrayLength = Array.getLength(array);
			Object[] newArray = (Object[])Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
			System.arraycopy(array, 0, newArray, 0, arrayLength);
			return newArray;
		}
		return (Object[])Array.newInstance(type, 1);
	}

	public static <Object> boolean contains(Object[] array, Object value)
	{
		if(array == null)
			return false;
		for(int i = 0; i < array.length; i++)
			if(value == array[i])
				return true;
		return false;
	}

	public static int indexOfS(String[] array, String value, int index)
	{
		if(index < 0 || array.length <= index)
			return -1;
		for(int i = index; i < array.length; i++)
			if(value.equals(array[i]))
				return i;
		return -1;
	}

	public static <Object> int indexOf(Object[] array, Object value, int index)
	{
		if(index < 0 || array.length <= index)
			return -1;
		for(int i = index; i < array.length; i++)
			if(value == array[i])
				return i;
		return -1;
	}

	@SuppressWarnings({"unchecked", "hiding"})
	public static <Object> Object[] remove(Object[] array, Object value)
	{
		if(array == null)
			return null;
		int index = indexOf(array, value, 0);

		if(index == -1)
			return array;
		int length = array.length;

		Object[] newArray = (Object[]) Array.newInstance(array.getClass().getComponentType(), length - 1);
		System.arraycopy(array, 0, newArray, 0, index);
		if(index < length - 1)
			System.arraycopy(array, index + 1, newArray, index, length - index - 1);
		return newArray;
	}

	public static String[] removes(String[] array, String value)
	{
		if(array == null)
			return null;
		int index = indexOfS(array, value, 0);

		if(index == -1)
			return array;
		int length = array.length;

		String[] newArray = new String[length - 1];
		System.arraycopy(array, 0, newArray, 0, index);
		if(index < length - 1)
			System.arraycopy(array, index + 1, newArray, index, length - index - 1);
		return newArray;
	}

	public static int[] toArray(Collection<Integer> collection)
	{
		int[] ar = new int[collection.size()];
		int i = 0;
		for(Integer t : collection)
			ar[(i++)] = t;
		return ar;
	}

	public static int[] createAscendingArray(int min, int max)
	{
		int length = max - min;
		int[] array = new int[length + 1];
		int x = 0;
		for(int i = min; i <= max; x++)
		{
			array[x] = i;
			i++;
		}
		return array;
	}

	public static long[][] add(long[][] arr, long o, long c)
	{
		if(arr == null)
		{
			arr = new long[1][2];
			arr[0][0] = o;
			arr[0][1] = c;
			return arr;
		}
		int len = arr.length;
		long[][] tmp = new long[len+1][2];
		System.arraycopy(arr, 0, tmp, 0, len);
		tmp[len][0] = o;
		tmp[len][1] = c;
		return tmp;
	}
}