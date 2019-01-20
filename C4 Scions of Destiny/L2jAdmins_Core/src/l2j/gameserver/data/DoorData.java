package l2j.gameserver.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.geoengine.geodata.ABlock;
import l2j.gameserver.geoengine.geodata.GeoStructure;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.templates.DoorTemplate;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.util.geometry.Polygon;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class DoorData extends XmlParser
{
	private static final Map<Integer, L2DoorInstance> staticsDoors = new HashMap<>();
	private static final Map<Integer, L2DoorInstance> castleDoors = new HashMap<>();
	
	@Override
	public void load()
	{
		loadFile("data/xml/doors.xml");
		UtilPrint.result("DoorData", "Loaded doors ", (staticsDoors.size() + castleDoors.size()));
	}
	
	@Override
	protected void parseFile()
	{
		for (Node n : getNodes("door"))
		{
			int posX = 0;
			int posY = 0;
			int posZ = 0;
			int height = 400;
			
			int id = 0;
			String name = "";
			boolean unlockable = false;
			int castleId = 0;
			boolean isWall = false;
			boolean isOpen = false;
			int openTask = -1;
			
			int hp = 0;
			int pDef = 0;
			int mDef = 0;
			
			List<int[]> coords = new ArrayList<>();
			int minX = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxY = Integer.MIN_VALUE;
			
			NamedNodeMap attrs = n.getAttributes();
			Node att;
			
			id = parseInt(attrs, "id");
			name = parseString(attrs, "name");
			unlockable = parseBoolean(attrs, "unlockable");
			
			att = attrs.getNamedItem("castleId");
			if (att != null)
			{
				castleId = parseInt(attrs, "castleId");
			}
			att = attrs.getNamedItem("isWall");
			if (att != null)
			{
				isWall = parseBoolean(attrs, "isWall");
			}
			att = attrs.getNamedItem("isOpen");
			if (att != null)
			{
				isOpen = parseBoolean(attrs, "isOpen");
			}
			att = attrs.getNamedItem("openTask");
			if (att != null)
			{
				openTask = parseInt(attrs, "openTask");
			}
			
			for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling())
			{
				attrs = c.getAttributes();
				
				switch (c.getNodeName())
				{
					case "height":
						// TODO missing
						break;
					case "position":
						posX = parseInt(attrs, "x");
						posY = parseInt(attrs, "y");
						posZ = parseInt(attrs, "z");
						break;
					case "stat":
						hp = parseInt(attrs, "hp");
						pDef = parseInt(attrs, "pDef");
						mDef = parseInt(attrs, "mDef");
						break;
					case "coordinates":
						for (Node d = c.getFirstChild(); d != null; d = d.getNextSibling())
						{
							if (!d.getNodeName().equals("loc"))
							{
								continue;
							}
							
							attrs = d.getAttributes();
							int x = parseInt(attrs, "x");
							int y = parseInt(attrs, "y");
							
							coords.add(new int[]
							{
								x,
								y
							});
							
							minX = Math.min(minX, x);
							maxX = Math.max(maxX, x);
							minY = Math.min(minY, y);
							maxY = Math.max(maxY, y);
						}
						break;
				}
			}
			
			// create basic description of door, taking extended outer dimensions of door
			final int x = GeoEngine.getGeoX(minX) - 1;
			final int y = GeoEngine.getGeoY(minY) - 1;
			final int sizeX = ((GeoEngine.getGeoX(maxX) + 1) - x) + 1;
			final int sizeY = ((GeoEngine.getGeoY(maxY) + 1) - y) + 1;
			
			// check door Z and adjust it
			final int geoX = GeoEngine.getGeoX(posX);
			final int geoY = GeoEngine.getGeoY(posY);
			final int geoZ = GeoEngine.getInstance().getHeightNearest(geoX, geoY, posZ);
			final ABlock block = GeoEngine.getInstance().getBlock(geoX, geoY);
			final int i = block.getIndexAbove(geoX, geoY, geoZ);
			if (i != -1)
			{
				final int layerDiff = block.getHeight(i) - geoZ;
				if (height > layerDiff)
				{
					height = layerDiff - GeoStructure.CELL_IGNORE_HEIGHT;
				}
			}
			
			final int limit = isWall ? GeoStructure.CELL_IGNORE_HEIGHT * 4 : GeoStructure.CELL_IGNORE_HEIGHT;
			
			// create 2D door description and calculate limit coordinates
			final boolean[][] inside = new boolean[sizeX][sizeY];
			final Polygon polygon = new Polygon(id, coords);
			for (int ix = 0; ix < sizeX; ix++)
			{
				for (int iy = 0; iy < sizeY; iy++)
				{
					// get geodata coordinates
					int gx = x + ix;
					int gy = y + iy;
					
					// check layer height
					int z = GeoEngine.getInstance().getHeightNearest(gx, gy, posZ);
					if (Math.abs(z - posZ) > limit)
					{
						continue;
					}
					
					// get world coordinates
					int worldX = GeoEngine.getWorldX(gx);
					int worldY = GeoEngine.getWorldY(gy);
					
					// set inside flag
					cell:
					for (int wix = worldX - 6; wix <= (worldX + 6); wix += 2)
					{
						for (int wiy = worldY - 6; wiy <= (worldY + 6); wiy += 2)
						{
							if (polygon.isInside(wix, wiy))
							{
								inside[ix][iy] = true;
								break cell;
							}
						}
					}
				}
			}
			
			StatsSet npcDat = new StatsSet();
			
			// set world coordinates
			npcDat.set("posX", posX);
			npcDat.set("posY", posY);
			npcDat.set("posZ", posZ);
			
			// set geodata coordinates and geodata
			npcDat.set("geoX", x);
			npcDat.set("geoY", y);
			npcDat.set("geoZ", geoZ);
			npcDat.set("geoData", GeoEngine.calculateGeoObject(inside));
			
			npcDat.set("name", name);
			npcDat.set("id", id);
			npcDat.set("level", 1);
			
			npcDat.set("accuracy", 38);
			npcDat.set("evasion", 38);
			npcDat.set("critRate", 38);
			
			npcDat.set("hpBase", hp);
			npcDat.set("hpReg", 3.e-3f);
			npcDat.set("mpReg", 3.e-3f);
			
			npcDat.set("pDef", pDef);
			npcDat.set("mDef", mDef);
			npcDat.set("collisionRadius", 16);
			npcDat.set("collisionHeight", height);
			
			npcDat.set("isWall", isWall);
			npcDat.set("unlockable", unlockable);
			npcDat.set("castleId", castleId);
			
			DoorTemplate template = new DoorTemplate(npcDat);
			
			L2DoorInstance door = new L2DoorInstance(IdFactory.getInstance().getNextId(), template);
			
			door.setXYZInvisible(posX, posY, posZ);
			
			door.spawnMe(posX, posY, posZ);
			door.setCurrentHpMp(door.getStat().getMaxHp(), door.getStat().getMaxMp());
			
			if (isOpen)
			{
				door.openMe();
			}
			else
			{
				door.closeMe();
			}
			
			if (openTask > 0)
			{
				door.setAutoOpenCloseTask(openTask);
			}
			
			if (castleId == 0)
			{
				staticsDoors.put(door.getId(), door);
			}
			else
			{
				castleDoors.put(door.getId(), door);
			}
			
			ClanHall clanhall = ClanHallData.getNearbyClanHall(door.getX(), door.getY(), 500);
			
			if (clanhall != null)
			{
				clanhall.getDoors().add(door);
				door.setClanHall(clanhall);
			}
		}
	}
	
	/**
	 * Obtenemos todas las puertas de los castillos
	 * @return Collection<L2DoorInstance>
	 */
	public Collection<L2DoorInstance> getCastleDoors()
	{
		return castleDoors.values();
	}
	
	/**
	 * Obtenemos todas las puertas excepto las de castillos
	 * @return L2DoorInstance
	 */
	public Collection<L2DoorInstance> getStaticDoors()
	{
		return staticsDoors.values();
	}
	
	/**
	 * Obtenemos un door a partir de su Id
	 * @param  key
	 * @return     L2DoorInstance
	 */
	public L2DoorInstance getDoor(int key)
	{
		if (staticsDoors.containsKey(key))
		{
			return staticsDoors.get(key);
		}
		if (castleDoors.containsKey(key))
		{
			return castleDoors.get(key);
		}
		
		return null;
	}
	
	public static DoorData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DoorData INSTANCE = new DoorData();
	}
}
