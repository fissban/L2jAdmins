package l2j.gameserver.geoengine.pathfinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import l2j.Config;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.geoengine.geodata.GeoStructure;

/**
 * @author DS, Hasha; Credits to Diamond
 */
public class NodeBuffer
{
	private final ReentrantLock lock = new ReentrantLock();
	private final int size;
	private final Node[][] buffer;
	
	// center coordinates
	private int cx = 0;
	private int cy = 0;
	
	// target coordinates
	private int gtx = 0;
	private int gty = 0;
	private short gtz = 0;
	
	// pathfinding statistics
	private long timeStamp = 0;
	private long lastElapsedTime = 0;
	
	private Node current = null;
	
	/**
	 * Constructor of NodeBuffer.
	 * @param size : one dimension size of buffer
	 */
	public NodeBuffer(int size)
	{
		// set size
		this.size = size;
		
		// initialize buffer
		buffer = new Node[size][size];
		for (int x = 0; x < size; x++)
		{
			for (int y = 0; y < size; y++)
			{
				buffer[x][y] = new Node();
			}
		}
	}
	
	/**
	 * Find path consisting of Nodes. Starts at origin coordinates, ends in target coordinates.
	 * @param  gox : origin point x
	 * @param  goy : origin point y
	 * @param  goz : origin point z
	 * @param  gtx : target point x
	 * @param  gty : target point y
	 * @param  gtz : target point z
	 * @return     Node : first node of path
	 */
	public final Node findPath(int gox, int goy, short goz, int gtx, int gty, short gtz)
	{
		// load timestamp
		timeStamp = System.currentTimeMillis();
		
		// set coordinates (middle of the line (gox,goy) - (gtx,gty), will be in the center of the buffer)
		cx = gox + ((gtx - gox - size) / 2);
		cy = goy + ((gty - goy - size) / 2);
		
		this.gtx = gtx;
		this.gty = gty;
		this.gtz = gtz;
		
		current = getNode(gox, goy, goz);
		current.setCost(getCostH(gox, goy, goz));
		
		int count = 0;
		do
		{
			// reached target?
			if ((current.getLoc().getGeoX() == gtx) && (current.getLoc().getGeoY() == gty) && (Math.abs(current.getLoc().getZ() - gtz) < 8))
			{
				return current;
			}
			
			// expand current node
			expand();
			
			// move pointer
			current = current.getChild();
		}
		while ((current != null) && (++count < Config.MAX_ITERATIONS));
		
		return null;
	}
	
	/**
	 * Creates list of Nodes to show debug path.
	 * @return List<Node> : nodes
	 */
	public final List<Node> debugPath()
	{
		List<Node> result = new ArrayList<>();
		
		for (Node n = current; n.getParent() != null; n = n.getParent())
		{
			result.add(n);
			n.setCost(-n.getCost());
		}
		
		for (Node[] nodes : buffer)
		{
			for (Node node : nodes)
			{
				if ((node.getLoc() == null) || (node.getCost() <= 0))
				{
					continue;
				}
				
				result.add(node);
			}
		}
		
		return result;
	}
	
	public final boolean isLocked()
	{
		return lock.tryLock();
	}
	
	public final void free()
	{
		current = null;
		
		for (Node[] nodes : buffer)
		{
			for (Node node : nodes)
			{
				if (node.getLoc() != null)
				{
					node.free();
				}
			}
		}
		
		lock.unlock();
		lastElapsedTime = System.currentTimeMillis() - timeStamp;
	}
	
	public final long getElapsedTime()
	{
		return lastElapsedTime;
	}
	
	/**
	 * Check current Node and add its neighbors to the buffer.
	 */
	private final void expand()
	{
		// can't move anywhere, don't expand
		byte nswe = current.getLoc().getNSWE();
		if (nswe == 0)
		{
			return;
		}
		
		// get geo coords of the node to be expanded
		final int x = current.getLoc().getGeoX();
		final int y = current.getLoc().getGeoY();
		final short z = (short) current.getLoc().getZ();
		
		// can move north, expand
		if ((nswe & GeoStructure.CELL_FLAG_N) != 0)
		{
			addNode(x, y - 1, z, Config.BASE_WEIGHT);
		}
		
		// can move south, expand
		if ((nswe & GeoStructure.CELL_FLAG_S) != 0)
		{
			addNode(x, y + 1, z, Config.BASE_WEIGHT);
		}
		
		// can move west, expand
		if ((nswe & GeoStructure.CELL_FLAG_W) != 0)
		{
			addNode(x - 1, y, z, Config.BASE_WEIGHT);
		}
		
		// can move east, expand
		if ((nswe & GeoStructure.CELL_FLAG_E) != 0)
		{
			addNode(x + 1, y, z, Config.BASE_WEIGHT);
		}
		
		// can move north-west, expand
		if ((nswe & GeoStructure.CELL_FLAG_NW) != 0)
		{
			addNode(x - 1, y - 1, z, Config.DIAGONAL_WEIGHT);
		}
		
		// can move north-east, expand
		if ((nswe & GeoStructure.CELL_FLAG_NE) != 0)
		{
			addNode(x + 1, y - 1, z, Config.DIAGONAL_WEIGHT);
		}
		
		// can move south-west, expand
		if ((nswe & GeoStructure.CELL_FLAG_SW) != 0)
		{
			addNode(x - 1, y + 1, z, Config.DIAGONAL_WEIGHT);
		}
		
		// can move south-east, expand
		if ((nswe & GeoStructure.CELL_FLAG_SE) != 0)
		{
			addNode(x + 1, y + 1, z, Config.DIAGONAL_WEIGHT);
		}
	}
	
	/**
	 * Returns node, if it exists in buffer.
	 * @param  x : node X coord
	 * @param  y : node Y coord
	 * @param  z : node Z coord
	 * @return   Node : node, if exits in buffer
	 */
	private final Node getNode(int x, int y, short z)
	{
		// check node X out of coordinates
		final int ix = x - cx;
		if ((ix < 0) || (ix >= size))
		{
			return null;
		}
		
		// check node Y out of coordinates
		final int iy = y - cy;
		if ((iy < 0) || (iy >= size))
		{
			return null;
		}
		
		// get node
		Node result = buffer[ix][iy];
		
		// check and update
		if (result.getLoc() == null)
		{
			result.setLoc(x, y, z);
		}
		
		// return node
		return result;
	}
	
	/**
	 * Add node given by coordinates to the buffer.
	 * @param x      : geo X coord
	 * @param y      : geo Y coord
	 * @param z      : geo Z coord
	 * @param weight : weight of movement to new node
	 */
	private final void addNode(int x, int y, short z, int weight)
	{
		// get node to be expanded
		Node node = getNode(x, y, z);
		if (node == null)
		{
			return;
		}
		
		// Z distance between nearby cells is higher than cell size, record as geodata bug
		if (node.getLoc().getZ() > (z + (2 * GeoStructure.CELL_HEIGHT)))
		{
			if (Config.DEBUG_GEO_NODE)
			{
				GeoEngine.getInstance().addGeoBug(node.getLoc(), "NodeBufferDiag: Check Z coords.");
			}
			
			return;
		}
		
		// node was already expanded, return
		if (node.getCost() >= 0)
		{
			return;
		}
		
		node.setParent(current);
		if (node.getLoc().getNSWE() != (byte) 0xFF)
		{
			node.setCost(getCostH(x, y, node.getLoc().getZ()) + (weight * Config.OBSTACLE_MULTIPLIER));
		}
		else
		{
			node.setCost(getCostH(x, y, node.getLoc().getZ()) + weight);
		}
		
		Node current = this.current;
		int count = 0;
		while ((current.getChild() != null) && (count < (Config.MAX_ITERATIONS * 4)))
		{
			count++;
			if (current.getChild().getCost() > node.getCost())
			{
				node.setChild(current.getChild());
				break;
			}
			current = current.getChild();
		}
		
		if (count >= (Config.MAX_ITERATIONS * 4))
		{
			System.err.println("Pathfinding: too long loop detected, cost:" + node.getCost());
		}
		
		current.setChild(node);
	}
	
	/**
	 * @param  x : node X coord
	 * @param  y : node Y coord
	 * @param  i : node Z coord
	 * @return   double : node cost
	 */
	private final double getCostH(int x, int y, int i)
	{
		final int dX = x - gtx;
		final int dY = y - gty;
		final int dZ = (i - gtz) / GeoStructure.CELL_HEIGHT;
		
		// return (Math.abs(dX) + Math.abs(dY) + Math.abs(dZ)) * Config.HEURISTIC_WEIGHT; // Manhattan distance
		return Math.sqrt((dX * dX) + (dY * dY) + (dZ * dZ)) * Config.HEURISTIC_WEIGHT; // Direct distance
	}
}
