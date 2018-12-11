package l2j.gameserver.data;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.instancemanager.spawn.AutoSpawnManager;
import l2j.gameserver.model.holder.AutoSpawnHolder;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class AutoSpawnData extends XmlParser
{
	private int spawnCount = 0;
	
	@Override
	public void load()
	{
		loadFile("data/xml/sevenSigns.xml");
		UtilPrint.result("AutoSpawnData", "Loaded auto spawns for seven signs", spawnCount);
		spawnCount = 0;
		loadFile("data/xml/autoSpawn.xml");
		UtilPrint.result("AutoSpawnData", "Loaded all auto spawns", spawnCount);
		spawnCount = 0;
	}
	
	@Override
	protected void parseFile()
	{
		for (Node n : getNodes("spawn"))
		{
			NamedNodeMap attrs = n.getAttributes();
			
			// int groupId = parseInteger(attrs, "groupId");
			int npcId = parseInt(attrs, "npcId");
			boolean defaultSpawn = parseBoolean(attrs, "defaultSpawn");
			boolean broadcastSpawn = parseBoolean(attrs, "broadcastSpawn");
			int initialDelay = parseInt(attrs, "initialDelay");
			int respawnDelay = parseInt(attrs, "respawnDelay");
			int despawnDelay = parseInt(attrs, "despawnDelay");
			
			List<LocationHolder> locs = new ArrayList<>();
			for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling())
			{
				if ("loc".equalsIgnoreCase(c.getNodeName()))
				{
					attrs = c.getAttributes();
					
					int x = parseInt(attrs, "x");
					int y = parseInt(attrs, "y");
					int z = parseInt(attrs, "z");
					int heading = parseInt(attrs, "heading");
					
					locs.add(new LocationHolder(x, y, z, heading));
				}
			}
			
			int objectId = IdFactory.getInstance().getNextId();
			AutoSpawnManager.getInstance().registerSpawn(new AutoSpawnHolder(objectId, npcId, initialDelay, respawnDelay, despawnDelay, broadcastSpawn, locs), defaultSpawn);
			spawnCount++;
		}
	}
	
	public static AutoSpawnData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public static class SingletonHolder
	{
		protected static final AutoSpawnData INSTANCE = new AutoSpawnData();
	}
}
