package someMath;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CollectionManupulation 
{

	public static <T> Set<Set<T>> powerSet(Set<T> origin)
	{
		Set<Set<T>> product = new HashSet<>();
		
		if(origin.isEmpty())return product;
		
		Set<T> smaller = new HashSet<>();
		smaller.addAll(origin);
		T t = catchRandomElementOfSet(origin);
		smaller.remove(t);

		if(origin.size()==1)
		{
			product.add(new HashSet<>()); //empty Set
			product.add(origin);          //Set with single Element since origin.size()=1
			
			return product;
		}
		
		for(Set<T> setN: powerSet(smaller))
		{
			Set<T> deviantSet = new HashSet<>();
			deviantSet.addAll(setN);
			deviantSet.add(t);
			product.add(setN);
			product.add(deviantSet);
		}
		
		return product;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Set<List<T>> cartesianProduct(Set<T>... origin)
	{
		List<Set<T>> transformedOrigin = new ArrayList<>();
		for(int n=0;n<origin.length;n++)transformedOrigin.add(origin[n]);
		
		return cartesianProduct(transformedOrigin);
	}
	
	public static <T> Set<List<T>> cartesianProduct(List<Set<T>> origin)
	{

		Set<List<T>> product = new HashSet<>();

		if(origin.isEmpty())return product;

		Set<T> minus = origin.get(0);

		List<Set<T>> smaller = new ArrayList<>();
		smaller.addAll(origin);
		smaller.remove(minus);

		if(origin.size()==1)
		{
			for(T t: minus)
			{
				List<T> l = new ArrayList<T>();
				l.add(t);
				product.add(l);
			}
			
			return product;//not necessary
		}

		for(T t: minus)
		{
			
			for(List<T> list: cartesianProduct(smaller))
			{
			
				List<T> tupel = new ArrayList<>();
				tupel.add(t);
				tupel.addAll(list);
				product.add(tupel);
			}
		}
				
		return product;
	}
	
	
	public static <T> T catchRandomElementOfSet(Set<T> set)
	{
		
		List<T> list = new ArrayList<>();
		list.addAll(set);
		
		return catchRandomElementOfList(list);
	}
	
	public static <T> T catchRandomElementOfList(List<T> list)
	{
		
		int n = list.size();
		
		return list.get(randomInt(n));
	}
	
	public static <T> T catchRandomElementOfArray(T[] arr)
	{
		
		int n = arr.length;
		
		return arr[randomInt(n)];
	}
	
	public static int randomInt(int n)
	{
		return (int)(Math.random()*n);
	}
}
