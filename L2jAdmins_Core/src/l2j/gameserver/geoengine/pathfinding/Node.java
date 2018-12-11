package l2j.gameserver.geoengine.pathfinding;

import l2j.gameserver.geoengine.geodata.GeoLocation;

/**
 * @author Hasha
 */
public class Node
{
	// node coords and nswe flag
	private GeoLocation loc;
	// node parent (for reverse path construction)
	private Node parent;
	// node child (for moving over nodes during iteration)
	private Node child;
	// node G cost (movement cost = parent movement cost + current movement cost)
	private double cost = -1000;
	
	public void setLoc(int x, int y, int z)
	{
		loc = new GeoLocation(x, y, z);
	}
	
	public GeoLocation getLoc()
	{
		return loc;
	}
	
	public void setParent(Node parent)
	{
		this.parent = parent;
	}
	
	public Node getParent()
	{
		return parent;
	}
	
	public void setChild(Node child)
	{
		this.child = child;
	}
	
	public Node getChild()
	{
		return child;
	}
	
	public void setCost(double cost)
	{
		this.cost = cost;
	}
	
	public double getCost()
	{
		return cost;
	}
	
	public void free()
	{
		// reset node location
		loc = null;
		// reset node parent, child and cost
		parent = null;
		child = null;
		cost = -1000;
	}
}
