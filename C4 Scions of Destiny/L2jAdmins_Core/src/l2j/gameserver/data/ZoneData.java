package l2j.gameserver.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Node;

import l2j.gameserver.instancemanager.zone.ZoneArenaManager;
import l2j.gameserver.instancemanager.zone.ZoneGrandBossManager;
import l2j.gameserver.instancemanager.zone.ZoneOlympiadStadiumManager;
import l2j.gameserver.instancemanager.zone.ZoneTownManager;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.model.zone.Zone;
import l2j.gameserver.model.zone.ZoneSpawn;
import l2j.gameserver.model.zone.form.ZoneCubo;
import l2j.gameserver.model.zone.form.ZoneNPoly;
import l2j.gameserver.model.zone.type.ArenaZone;
import l2j.gameserver.model.zone.type.BossZone;
import l2j.gameserver.model.zone.type.OlympiadStadiumZone;
import l2j.gameserver.model.zone.type.TownZone;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * This class manages all zone data.
 * @author fissban
 */
public class ZoneData extends XmlParser
{
	private static final Logger LOG = Logger.getLogger(ZoneData.class.getName());
	
	private static final Map<Integer, Zone> zones = new HashMap<>();
	
	// Constructor
	public ZoneData()
	{
		load();
		
	}
	
	@Override
	public void load()
	{
		loadFile("data/xml/zone.xml");
		UtilPrint.result("ZoneData", "Loaded zones", zones.size());
	}
	
	public void reLoad()
	{
		//
	}
	
	@Override
	protected void parseFile()
	{
		try
		{
			for (var n : getNodes("zone"))
			{
				var attrs = n.getAttributes();
				var id = parseInt(attrs, "id");
				var minZ = parseInt(attrs, "minZ");
				var maxZ = parseInt(attrs, "maxZ");
				var type = parseString(attrs, "type");
				var shape = parseString(attrs, "shape");
				
				Zone zone = null;
				try
				{
					zone = (Zone) Class.forName("l2j.gameserver.model.zone.type." + type + "Zone").getConstructors()[0].newInstance(id);
				}
				catch (Exception e)
				{
					System.out.println("Worng zone " + type);
					e.printStackTrace();
					continue;
				}
				
				// Check for unknown type
				if (zone == null)
				{
					LOG.warning("ZoneData: No such zone type: " + type);
					continue;
				}
				
				// Check for additional parameters
				for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling())
				{
					switch (c.getNodeName())
					{
						case "stat":
						{
							attrs = c.getAttributes();
							
							var name = parseString(attrs, "name");
							var val = parseString(attrs, "value");
							
							zone.setParameter(name, val);
							break;
						}
						case "spawn":
						{
							attrs = c.getAttributes();
							
							var spawnX = parseInt(attrs, "x");
							var spawnY = parseInt(attrs, "y");
							var spawnZ = parseInt(attrs, "z");
							
							var isChaotic = parseBoolean(attrs, "isChaotic");
							
							if (isChaotic)
							{
								((ZoneSpawn) zone).addChaoticSpawn(spawnX, spawnY, spawnZ);
							}
							else
							{
								((ZoneSpawn) zone).addSpawn(spawnX, spawnY, spawnZ);
							}
							break;
						}
						case "vertices":
						{
							if (shape.equals("Cubo"))
							{
								int[] x = new int[2];
								int[] y = new int[2];
								
								int locs = 0;
								for (Node d = c.getFirstChild(); d != null; d = d.getNextSibling())
								{
									if (!d.getNodeName().equalsIgnoreCase("loc"))
									{
										continue;
									}
									attrs = d.getAttributes();
									
									x[locs] = parseInt(attrs, "x");
									y[locs] = parseInt(attrs, "y");
									
									locs++;
									if (locs == 2)
									{
										break;
									}
								}
								zone.setForm(new ZoneCubo(x[0], x[1], y[0], y[1], minZ, maxZ));
							}
							else
							{
								var flx = new ArrayList<Integer>();
								var fly = new ArrayList<Integer>();
								
								for (Node d = c.getFirstChild(); d != null; d = d.getNextSibling())
								{
									if (!d.getNodeName().equalsIgnoreCase("loc"))
									{
										continue;
									}
									
									attrs = d.getAttributes();
									
									flx.add(parseInt(attrs, "x"));
									fly.add(parseInt(attrs, "y"));
								}
								
								zone.setForm(new ZoneNPoly(flx.toArray(new Integer[flx.size()]), fly.toArray(new Integer[fly.size()]), minZ, maxZ));
							}
							
							break;
						}
					}
				}
				
				// Get the world regions
				var worldRegions = L2World.getInstance().getAllWorldRegions();
				// Register the zone into any world region it intersects with...
				for (int x = 0; x < worldRegions.length; x++)
				{
					for (var y = 0; y < worldRegions[x].length; y++)
					{
						if (zone.getZone().intersectsRectangle(L2World.getRegionX(x), L2World.getRegionX(x + 1), L2World.getRegionY(y), L2World.getRegionY(y + 1)))
						{
							worldRegions[x][y].addZone(zone);
						}
					}
				}
				
				// Special managers for arenas, towns...
				if (zone instanceof ArenaZone)
				{
					ZoneArenaManager.add((ArenaZone) zone);
				}
				else if (zone instanceof TownZone)
				{
					ZoneTownManager.add((TownZone) zone);
				}
				else if (zone instanceof OlympiadStadiumZone)
				{
					ZoneOlympiadStadiumManager.add((OlympiadStadiumZone) zone);
				}
				else if (zone instanceof BossZone)
				{
					ZoneGrandBossManager.add((BossZone) zone);
				}
				
				zones.put(zone.getId(), zone);
			}
			
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Error while loading zones.", e);
			e.printStackTrace();
			return;
		}
		
		ZoneGrandBossManager.getInstance().initZones();
	}
	
	public List<Zone> getZones(int x, int y)
	{
		var region = L2World.getInstance().getRegion(x, y);
		var temp = new ArrayList<Zone>();
		for (var zone : region.getZones())
		{
			if (zone.isInsideZone(x, y))
			{
				temp.add(zone);
			}
		}
		return temp;
	}
	
	public static ZoneData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ZoneData INSTANCE = new ZoneData();
	}
}
