package someMath;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.util.Pair;

public class DirectedWeightedGraph <ID>
{          //

	private final Set<ID> idSet = new HashSet<>();
	private final Map<Pair<ID, ID>, Double> connections = new HashMap<>();
	//TODO: make use of -> private final Map<String, ID> tags = new HashMap<>();
	
	public DirectedWeightedGraph(Set<ID> idSet)
	{
		this.idSet.addAll(idSet);
	}
	
	public void connect(ID id1, ID id2, double weight)
	{
		
		if(!idSet.contains(id1)) throw new IllegalArgumentException("Can't connect. ID is not in idSet");
		if(!idSet.contains(id2)) throw new IllegalArgumentException("Can't connect. ID is not in idSet");

		connections.put(new Pair<ID, ID>(id1, id2), weight);
	}
	
	public Set<ID> getDestinysOf(ID id)
	{
		
		Set<ID> destinys = new HashSet<>();
		for(Pair<ID, ID> pairs: connections.keySet())
		{
			if(id.equals(pairs.getKey()))destinys.add(pairs.getValue());
		}
		
		return destinys;
	}
}