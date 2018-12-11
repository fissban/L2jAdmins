package l2j.gameserver.geoengine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import l2j.Config;
import l2j.gameserver.geoengine.geodata.GeoLocation;
import l2j.gameserver.geoengine.pathfinding.Node;
import l2j.gameserver.geoengine.pathfinding.NodeBuffer;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.UtilPrint;

/**
 * @author Hasha
 */
final class GeoEnginePathfinding extends GeoEngine
{
	// pre-allocated buffers
	private final BufferHolder[] buffers;
	
	// pathfinding statistics
	private int findSuccess = 0;
	private int findFails = 0;
	private int postFilterPlayableUses = 0;
	private int postFilterUses = 0;
	private long postFilterElapsed = 0;
	
	protected GeoEnginePathfinding()
	{
		super();
		
		String[] array = Config.PATHFIND_BUFFERS.split(";");
		buffers = new BufferHolder[array.length];
		
		int count = 0;
		for (int i = 0; i < array.length; i++)
		{
			String buf = array[i];
			String[] args = buf.split("x");
			
			try
			{
				int size = Integer.parseInt(args[1]);
				count += size;
				buffers[i] = new BufferHolder(Integer.parseInt(args[0]), size);
			}
			catch (Exception e)
			{
				LOG.warning("GeoEnginePathfinding: Can not load buffer setting: " + buf);
			}
		}
		
		UtilPrint.result("GeoEnginePathfinding", "Loaded node buffers", count);
	}
	
	@Override
	public List<LocationHolder> findPath(int ox, int oy, int oz, int tx, int ty, int tz, boolean playable)
	{
		// get origin and check existing geo coords
		int gox = getGeoX(ox);
		int goy = getGeoY(oy);
		if (!hasGeoPos(gox, goy))
		{
			return null;
		}
		
		short goz = getHeightNearest(gox, goy, oz);
		
		// get target and check existing geo coords
		int gtx = getGeoX(tx);
		int gty = getGeoY(ty);
		if (!hasGeoPos(gtx, gty))
		{
			return null;
		}
		
		short gtz = getHeightNearest(gtx, gty, tz);
		
		// Prepare buffer for pathfinding calculations
		NodeBuffer buffer = getBuffer(64 + (2 * Math.max(Math.abs(gox - gtx), Math.abs(goy - gty))), playable);
		if (buffer == null)
		{
			return null;
		}
		
		// clean debug path
		boolean debug = playable && Config.DEBUG_PATH;
		if (debug)
		{
			clearDebugItems();
		}
		
		// find path
		List<LocationHolder> path = null;
		try
		{
			Node result = buffer.findPath(gox, goy, goz, gtx, gty, gtz);
			
			if (result == null)
			{
				findFails++;
				return null;
			}
			
			if (debug)
			{
				// path origin
				dropDebugItem(728, 0, new GeoLocation(gox, goy, goz)); // blue potion
				
				// path
				for (Node n : buffer.debugPath())
				{
					if (n.getCost() < 0)
					{
						dropDebugItem(1831, (int) (-n.getCost() * 10), n.getLoc()); // antidote
					}
					else
					{
						dropDebugItem(57, (int) (n.getCost() * 10), n.getLoc()); // adena
					}
				}
			}
			
			path = constructPath(result);
		}
		catch (Exception e)
		{
			LOG.warning(e.getMessage());
			findFails++;
			return null;
		}
		finally
		{
			buffer.free();
			findSuccess++;
		}
		
		// check path
		if (path.size() < 3)
		{
			return path;
		}
		
		// log data
		long timeStamp = System.currentTimeMillis();
		postFilterUses++;
		if (playable)
		{
			postFilterPlayableUses++;
		}
		
		// get path list iterator
		ListIterator<LocationHolder> point = path.listIterator();
		
		// get node A (origin)
		int nodeAx = gox;
		int nodeAy = goy;
		short nodeAz = goz;
		
		// get node B
		GeoLocation nodeB = (GeoLocation) point.next();
		
		// iterate thought the path to optimize it
		while (point.hasNext())
		{
			// get node C
			GeoLocation nodeC = (GeoLocation) path.get(point.nextIndex());
			
			// check movement from node A to node C
			GeoLocation loc = checkMove(nodeAx, nodeAy, nodeAz, nodeC.getGeoX(), nodeC.getGeoY(), nodeC.getZ());
			if ((loc.getGeoX() == nodeC.getGeoX()) && (loc.getGeoY() == nodeC.getGeoY()))
			{
				// can move from node A to node C
				
				// remove node B
				point.remove();
				
				// show skipped nodes
				if (debug)
				{
					dropDebugItem(735, 0, nodeB); // green potion
				}
			}
			else
			{
				// can not move from node A to node C
				
				// set node A (node B is part of path, update A coordinates)
				nodeAx = nodeB.getGeoX();
				nodeAy = nodeB.getGeoY();
				nodeAz = (short) nodeB.getZ();
			}
			
			// set node B
			nodeB = (GeoLocation) point.next();
		}
		
		// show final path
		if (debug)
		{
			for (LocationHolder node : path)
			{
				dropDebugItem(65, 0, node); // red potion
			}
		}
		
		// log data
		postFilterElapsed += System.currentTimeMillis() - timeStamp;
		
		return path;
	}
	
	/**
	 * Create list of node locations as result of calculated buffer node tree.
	 * @param  target : the entry point
	 * @return        List<NodeLoc> : list of node location
	 */
	private static final List<LocationHolder> constructPath(Node target)
	{
		// create empty list
		LinkedList<LocationHolder> list = new LinkedList<>();
		
		// set direction X/Y
		int dx = 0;
		int dy = 0;
		
		// get target parent
		Node parent = target.getParent();
		
		// while parent exists
		while (parent != null)
		{
			// get parent <> target direction X/Y
			final int nx = parent.getLoc().getGeoX() - target.getLoc().getGeoX();
			final int ny = parent.getLoc().getGeoY() - target.getLoc().getGeoY();
			
			// direction has changed?
			if ((dx != nx) || (dy != ny))
			{
				// add node to the beginning of the list
				list.addFirst(target.getLoc());
				
				// update direction X/Y
				dx = nx;
				dy = ny;
			}
			
			// move to next node, set target and get its parent
			target = parent;
			parent = target.getParent();
		}
		
		// return list
		return list;
	}
	
	/**
	 * Provides optimize selection of the buffer. When all pre-initialized buffer are locked, creates new buffer and log this situation.
	 * @param  size     : pre-calculated minimal required size
	 * @param  playable : moving object is playable?
	 * @return          NodeBuffer : buffer
	 */
	private final NodeBuffer getBuffer(int size, boolean playable)
	{
		NodeBuffer current = null;
		for (BufferHolder holder : buffers)
		{
			// Find proper size of buffer
			if (holder.size < size)
			{
				continue;
			}
			
			// Find unlocked NodeBuffer
			for (NodeBuffer buffer : holder.buffer)
			{
				if (!buffer.isLocked())
				{
					continue;
				}
				
				holder.uses++;
				if (playable)
				{
					holder.playableUses++;
				}
				
				holder.elapsed += buffer.getElapsedTime();
				return buffer;
			}
			
			// NodeBuffer not found, allocate temporary buffer
			current = new NodeBuffer(holder.size);
			current.isLocked();
			
			holder.overflows++;
			if (playable)
			{
				holder.playableOverflows++;
			}
		}
		
		return current;
	}
	
	/**
	 * NodeBuffer container with specified size and count of separate buffers.
	 */
	private static final class BufferHolder
	{
		final int size;
		final int count;
		ArrayList<NodeBuffer> buffer;
		
		// statistics
		int playableUses = 0;
		int uses = 0;
		int playableOverflows = 0;
		int overflows = 0;
		long elapsed = 0;
		
		public BufferHolder(int size, int count)
		{
			this.size = size;
			this.count = count;
			buffer = new ArrayList<>(count);
			
			for (int i = 0; i < count; i++)
			{
				buffer.add(new NodeBuffer(size));
			}
		}
		
		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder(100);
			
			sb.append("Buffer " + String.valueOf(size) + "x" + String.valueOf(size) + ": count=" + String.valueOf(count) + " uses=" + String.valueOf(playableUses) + "/" + String.valueOf(uses));
			
			if (uses > 0)
			{
				sb.append(" total/avg(ms)=" + String.valueOf(elapsed) + "/" + String.format("%1.2f" + ((double) elapsed / uses)));
			}
			
			sb.append(" ovf=" + String.valueOf(playableOverflows) + "/" + String.valueOf(overflows));
			
			return sb.toString();
		}
	}
	
	@Override
	public List<String> getStat()
	{
		List<String> list = new ArrayList<>();
		
		for (BufferHolder buffer : buffers)
		{
			list.add(buffer.toString());
		}
		
		list.add("Use: playable=" + String.valueOf(postFilterPlayableUses) + " non-playable=" + String.valueOf(postFilterUses - postFilterPlayableUses));
		
		if (postFilterUses > 0)
		{
			list.add("Time (ms): total=" + String.valueOf(postFilterElapsed) + " avg=" + String.format("%1.2f", (double) postFilterElapsed / postFilterUses));
		}
		
		list.add("Pathfind: success=" + String.valueOf(findSuccess) + ", fail=" + String.valueOf(findFails));
		
		return list;
	}
}
